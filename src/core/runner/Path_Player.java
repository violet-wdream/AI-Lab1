package core.runner;

import core.solver.queue.Node;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.VideoMode;
import org.jsfml.window.Window;
import org.jsfml.window.event.Event;
import stud.problem.puzzlePathfinding.Direction;
import stud.problem.puzzlePathfinding.Move;
import stud.problem.puzzlePathfinding.Position;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Path_Player {
    private RenderWindow window;
    private int size;
    private List<Text> texts;
    private List<Sprite> sprites; // when using image tiles
    private static Font font;
    private Texture fullTexture; // full source image
    private boolean useImage = false;
    private String imagePath = null;
    private int tilePixelSize = 150; // desired pixel size per tile when using image

    public Path_Player(){}

    static {
        font = new Font();
        try{
            font.loadFromFile(Paths.get("C:\\Windows\\Fonts\\comic.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(){
        window = new RenderWindow(new VideoMode(1100, 800), "NPUZZLE", Window.DEFAULT);
        window.setFramerateLimit(100);
    }

    /**
     * 设置用于拼图的图片路径（可为绝对路径或相对路径）。
     * 调用此方法后，Path_Player 会在 GUI 中使用图片切片来渲染每个格子。
     */
    public void setImage(String path) {
        if (path == null) return;
        this.imagePath = path;
        try {
            fullTexture = new Texture();
            fullTexture.loadFromFile(Paths.get(path));
            useImage = true;
        } catch (IOException e) {
            System.err.println(STR."Failed to load image '\{path}': \{e.getMessage()}");
            useImage = false;
            fullTexture = null;
        }
    }

    /**
     * 设置用于渲染时每个格子的像素大小（默认150）。
     */
    public void setTilePixelSize(int px) {
        if (px > 16) this.tilePixelSize = px;
    }

    public void update(Node node)
    {
        this.size=((Position)node.getState()).size;
        texts = new ArrayList<>();
        sprites = new ArrayList<>();

        Position pos = (Position) node.getState();

        if (useImage && fullTexture != null) {
            int texW = fullTexture.getSize().x;
            int texH = fullTexture.getSize().y;
            if (texW < size || texH < size) {
                System.err.println(STR."Image too small for \{size}x\{size} tiles, falling back to text mode.");
                useImage = false;
            } else {
                int tileW = texW / size;
                int tileH = texH / size;
                float scaleX = tilePixelSize / (float) tileW;
                float scaleY = tilePixelSize / (float) tileH;

                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        int val = pos.state[i][j];
                        if (val == 0) {
                            // create a transparent sprite for the blank tile
                            Sprite s = new Sprite();
                            s.setTexture(fullTexture);
                            s.setTextureRect(new IntRect(j * tileW, i * tileH, tileW, tileH));
                            s.setScale(scaleX, scaleY);
                            s.setPosition(j * tilePixelSize, i * tilePixelSize);
                            s.setColor(new Color(0, 0, 0, 0)); // fully transparent
                            sprites.add(s);
                        } else {
                            int tileIndex = val - 1; // 0-based
                            int srcRow = tileIndex / size;
                            int srcCol = tileIndex % size;
                            Sprite s = new Sprite();
                            s.setTexture(fullTexture);
                            s.setTextureRect(new IntRect(srcCol * tileW, srcRow * tileH, tileW, tileH));
                            s.setScale(scaleX, scaleY);
                            s.setPosition(j * tilePixelSize, i * tilePixelSize);
                            sprites.add(s);
                        }
                    }
                }
            }
        } else {
            // fallback: text rendering (as before)
            for(int i=0;i<size;++i){
                for(int j=0;j<size;++j){
                    Text text;
                    if(pos.state[i][j]==0){
                        text = new Text(" ",font);
                    }else {
                        text = new Text(Integer.toString(pos.state[i][j]), font);
                    }
                    text.setCharacterSize(70);// Text 字体大小为 70
                    text.setPosition(j*150,i*150);
                    texts.add(text);
                }
            }
        }
    }

    public void display(Deque<Node> path, String result) {
        Text re = new Text(result, font);
        re.setCharacterSize(33);
        re.setPosition(0, 600);
        Node node = path.removeFirst();
        Clock clock = new Clock();
        Time elsaptime = Time.getSeconds(0);

        update(node);
        int[] offsets = Direction.offset(((Move) path.getFirst().getAction()).getDirection());
        int xx, yy;
        xx = ((Position) node.getState()).blank[0] + offsets[0];
        yy = ((Position) node.getState()).blank[1] + offsets[1];

        Text currentStepText = new Text("", font);
        currentStepText.setCharacterSize(60);
        currentStepText.setPosition(600, 200);
        int stepNumber = 0;

        writeMatrixStateToFile(node, stepNumber, "path_states.txt");

        while (window.isOpen()) {
            // 使用时钟累积时间，用于控制动画节奏
            Time st = clock.restart();
            elsaptime = Time.add(elsaptime, st);
            float elapsedSec = elsaptime.asSeconds();

            window.clear(new Color(94, 0, 162));

            for (Event event : window.pollEvents()) {
                if (event.type == Event.Type.CLOSED) {
                    System.out.println("close");
                    window.close();
                }
            }

            currentStepText.setString(STR."Step \{stepNumber}");
            window.draw(currentStepText);

            // 动画过渡：如果使用图片，则移动 sprite，否则移动 texts
            if (useImage && sprites != null && !sprites.isEmpty()) {
                if (offsets != null) {
                    Sprite moving = sprites.get(xx * size + yy);
                    Sprite target = sprites.get((xx - offsets[0]) * size + (yy - offsets[1]));
                    float dx = moving.getPosition().x - target.getPosition().x;
                    float dy = moving.getPosition().y - target.getPosition().y;
                    if (Math.abs(dx) > 1f || Math.abs(dy) > 1f) {
                        // move sprite toward target in pixels
                        moving.move(-offsets[1] * 15, -offsets[0] * 15);
                    } else {
                        // snap to exact target to avoid overlap accumulation
                        moving.setPosition(target.getPosition());
                        elsaptime = Time.getSeconds(0);
                            if (!path.isEmpty()) {
                                node = path.removeFirst();
                                update(node);
                                // normalize positions after rebuild to avoid residual offsets
                                normalizeSpritesAndTexts();
                                stepNumber++; // 节点步数增加

                                // 写文件
                                writeMatrixStateToFile(node, stepNumber, "path_states.txt");

                                if (!path.isEmpty()) {
                                    offsets = Direction.offset(((Move) path.getFirst().getAction()).getDirection());
                                    xx = ((Position) node.getState()).blank[0] + offsets[0];
                                    yy = ((Position) node.getState()).blank[1] + offsets[1];
                                }
                        } else {
                            offsets = null;
                            // add result text to sprites area by drawing text separately
                        }
                    }
                }
            } else {
                if (offsets != null && !texts.get(xx * size + yy).getPosition().equals(
                        texts.get((xx - offsets[0]) * size + (yy - offsets[1])).getPosition())) {
                    Text movingT = texts.get(xx * size + yy);
                    Text targetT = texts.get((xx - offsets[0]) * size + (yy - offsets[1]));
                    float dxT = movingT.getPosition().x - targetT.getPosition().x;
                    float dyT = movingT.getPosition().y - targetT.getPosition().y;
                    if (Math.abs(dxT) > 1f || Math.abs(dyT) > 1f) {
                        movingT.move(-offsets[1] * 5, -offsets[0] * 5);
                    } else {
                        movingT.setPosition(targetT.getPosition());
                    }
                } else {
                    elsaptime = Time.getSeconds(0);
                        if (!path.isEmpty()) {
                        node = path.removeFirst();
                        update(node);
                        // normalize positions after rebuild to avoid residual offsets
                        normalizeSpritesAndTexts();
                        stepNumber++; // 节点步数增加

                        // 写文件
                        writeMatrixStateToFile(node, stepNumber, "path_states.txt");

                        if (!path.isEmpty()) {
                            offsets = Direction.offset(((Move) path.getFirst().getAction()).getDirection());
                            xx = ((Position) node.getState()).blank[0] + offsets[0];
                            yy = ((Position) node.getState()).blank[1] + offsets[1];
                        }
                    } else {
                        offsets = null;
                        texts.add(re);
                    }
                }
            }

            if (useImage && sprites != null) {
                for (Sprite sp : sprites) {
                    window.draw(sp);
                }
                // draw result text below
                if (offsets == null) window.draw(re);
            } else {
                for (Text e : texts) {
                    window.draw(e);
                }
            }

            window.display();

        }
    }
    private void writeMatrixStateToFile(Node node, int step, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) { // 使用 try-with-resources 写文件
            writer.write(STR."""
Step \{step}:
""");
            Position position = (Position) node.getState();
            int[][] state = position.state;
            for (int i = 0; i < state.length; i++) {
                for (int j = 0; j < state[i].length; j++) {
                    writer.write(STR."\{state[i][j]} ");
                }
                writer.write("\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    void writeResultToFile(String result, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) { // 使用 try-with-resources 写文件
            writer.write( result );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // 将当前 sprites 和 texts 的位置精确设置到网格坐标，避免动画残留偏移
    private void normalizeSpritesAndTexts() {
        if (sprites != null && !sprites.isEmpty()) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int idx = i * size + j;
                    if (idx < sprites.size()) {
                        Sprite sp = sprites.get(idx);
                        sp.setPosition(j * tilePixelSize, i * tilePixelSize);
                    }
                }
            }
        }
        if (texts != null && !texts.isEmpty()) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int idx = i * size + j;
                    if (idx < texts.size()) {
                        Text t = texts.get(idx);
                        t.setPosition(j * tilePixelSize, i * tilePixelSize);
                    }
                }
            }
        }
    }

}

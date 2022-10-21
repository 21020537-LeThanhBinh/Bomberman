package Bomberman.UI;

import static Bomberman.Constants.Constant.MAX_SCENE_HEIGHT;
import static Bomberman.Constants.Constant.MAX_SCENE_WIDTH;
import static Bomberman.Sounds.SoundEffect.turnOnMusic;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.play;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class StageStartScene extends SubScene {
    public StageStartScene() {
        play("stage_start.wav");

        var background = new Rectangle(MAX_SCENE_WIDTH, MAX_SCENE_HEIGHT, Color.color(0, 0, 0, 1));

        var title = getUIFactoryService().newText("Stage " + geti("level"), Color.WHITE, 40);
        title.setStroke(Color.WHITESMOKE);
        title.setStrokeWidth(1.5);
        title.setEffect(new Bloom(0.6));
        title.setX(MAX_SCENE_WIDTH / 3);
        title.setY(MAX_SCENE_HEIGHT / 2);
        getContentRoot().getChildren().addAll(background, title);

        animationBuilder()
                .onFinished(() -> popSubScene())
                .duration(Duration.seconds(4))
                .fade(getContentRoot())
                .from(1)
                .to(1)
                .buildAndPlay(this);
    }

    public void popSubScene() {
        turnOnMusic();
        getSceneService().popSubScene();
    }

}

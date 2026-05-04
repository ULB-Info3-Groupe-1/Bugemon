package ulb.views;

// import java.io.File;
// import javafx.fxml.FXML;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;

import ulb.Configuration;
// import ulb.common.dto.BugemonDTO;
// import ulb.models.level_up.LevelUp;

public class SkillTreeView extends View {
    // Skill:
    // Click on a skill node
    // Use an algorithm to dispatch properly the node...
    // Node should have colors and unlocked or not...
    // Drawing the link between node ? Spline or straight line ?? Reingold–Tilford layout

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SKILL_TREE_VIEW;
    }

    @Override
    public void refresh() {

    }
}

package components;

import engine.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void start() {

    }
}

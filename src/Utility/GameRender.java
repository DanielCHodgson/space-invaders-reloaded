package Utility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameRender {

  private List<DrawableGameObject> components = new ArrayList<>();


    public void addDrawableGameComponents (DrawableGameObject... dgc){
      components.addAll(Arrays.asList(dgc));

    }

    public  <T extends DrawableGameObject> void  addAllDrawableGameComponents (List<T> list) {
      components.addAll(list);


    }

    public List<DrawableGameObject> getComponents() {
      return components;
    }

}

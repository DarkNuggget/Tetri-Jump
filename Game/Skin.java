public class Skin {
    private final String name;
    private final String imagePath;
    private boolean bought;

    public Skin(String name, String imagePath, boolean bought) {
        this.name = name;
        this.imagePath = imagePath;
        this.bought = bought;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}

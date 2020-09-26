package celtech.coreUI.components.material;

import celtech.roboxbase.configuration.Filament;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 *
 * @author Ian
 */
public class FilamentOnReelDisplay extends HBox
{

    private final Text titleText = new Text("Nothing");
    private final Text filamentNameText = new Text();
    private final FilamentSwatch swatch = new FilamentSwatch();

    public FilamentOnReelDisplay()
    {
        initialise();
    }

    public FilamentOnReelDisplay(String title, Filament filament)
    {
        initialise();
        updateFilamentOnReelDisplay(title, filament);
    }

    private void initialise()
    {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5.0);
        SVGPath reelIcon = new SVGPath();
        reelIcon.setContent(
                "m 17.0867,23 c 0,-1.6092 1.3044,-2.9136 2.9133,-2.9136 1.6083,0 2.9127,1.3044 2.9133,2.9136 H 26 v -0.8517 c 0,-0.0678 -0.021,-0.129 -0.0627,-0.1836 -0.0417,-0.0546 -0.0966,-0.087 -0.1641,-0.0975 l -1.4298,-0.2187 c -0.078,-0.2499 -0.1851,-0.5052 -0.3204,-0.7656 0.0939,-0.1302 0.2343,-0.3138 0.4218,-0.5508 0.1875,-0.237 0.3204,-0.4098 0.3984,-0.5193 0.0417,-0.0573 0.0627,-0.1173 0.0627,-0.1797 0,-0.0729 -0.0183,-0.1305 -0.0546,-0.1719 -0.1875,-0.2658 -0.6177,-0.7083 -1.2891,-1.3281 -0.0627,-0.0522 -0.1278,-0.0783 -0.1956,-0.0783 -0.078,0 -0.1407,0.0234 -0.1875,0.0702 L 22.07,18.9608 C 21.8561,18.8516 21.6218,18.755 21.3668,18.6716 l -0.219,-1.4373 C 21.1424,17.1665 21.1133,17.1107 21.0581,17.0663 21.0038,17.0222 20.9399,17 20.8667,17 H 19.133 c -0.1512,0 -0.2448,0.0729 -0.2814,0.2187 -0.0681,0.2607 -0.1437,0.7449 -0.2268,1.4532 -0.2448,0.0786 -0.4815,0.1773 -0.7107,0.297 L 16.8353,18.1331 C 16.7681,18.0809 16.7,18.0548 16.6325,18.0548 c -0.1149,0 -0.3609,0.1863 -0.7383,0.5586 -0.378,0.3723 -0.6339,0.6522 -0.7692,0.8397 -0.0468,0.0678 -0.0708,0.1278 -0.0708,0.18 0,0.0624 0.0264,0.1248 0.0786,0.1875 0.3486,0.4215 0.6276,0.7812 0.8361,1.0779 -0.1308,0.2397 -0.2319,0.4794 -0.3048,0.7188 l -1.4538,0.2187 c -0.0567,0.0105 -0.1065,0.0444 -0.1482,0.1017 C 14.0204,21.9947 14,22.0547 14,22.1171 V 23 h 3.0867 z");
        getChildren().addAll(titleText, swatch, filamentNameText, reelIcon);
    }

    public final void updateFilamentOnReelDisplay(String title, Filament filament)
    {
        titleText.setText(title);
        filamentNameText.setText(filament.getFriendlyFilamentName());
        swatch.updateFilament(filament);
    }

    public Filament getSelectedFilament()
    {
        return swatch.getFilament();
    }
}

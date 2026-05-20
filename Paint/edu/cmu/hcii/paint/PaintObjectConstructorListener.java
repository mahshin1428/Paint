package edu.cmu.hcii.paint;

public interface PaintObjectConstructorListener {

    void constructionBeginning(PaintObject temporaryObject);
    void constructionContinuing(PaintObject temporaryObject);
    void constructionComplete(PaintObject finalObject);
    void hoveringOverConstructionArea(PaintObject hoverObject);

}

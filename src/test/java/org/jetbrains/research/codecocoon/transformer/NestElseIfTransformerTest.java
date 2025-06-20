package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NestElseIfTransformerTest {

    Transformer transformer = new NestElseIfTransformer();

    @Test
    public void testSimple()  {
        String inputCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 1;
                  } else if (i == 2) {
                    return 2;
                  }
                  return i;
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 1;
                  } else {
                    if (i == 2) {
                      return 2;
                    }
                  }
                  return i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.NestElseIf));
    }

    @Test
    public void testNested()  {
        String inputCode = """
                public int test(int i) {
                  if(i < 0) {
                    if (i == 1) {
                      return 1;
                    } else if (i == 2) {
                      return 2;
                    }
                  }
                  return i;
                }""";
        String expectedCode = """
                public int test(int i) {
                  if(i < 0) {
                    if (i == 1) {
                      return 1;
                    } else {
                      if (i == 2) {
                        return 2;
                      }
                    }
                  }
                  return i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.NestElseIf));
    }

    @Test
    public void testElseIfElse()  {
        String inputCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 1;
                  } else if (i == 2) {
                    return 2;
                  } else {
                    return i;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i == 1) {
                    return 1;
                  } else {
                    if (i == 2) {
                      return 2;
                    } else {
                      return i;
                    }
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.NestElseIf));
    }

    @Test
    public void testNotApplicable()  {
        String inputCode = """
                public int test(boolean value) {
                  return !value;
                }""";
        String expectedCode = """
                public int test(boolean value) {
                  return !value;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.NestElseIf));
    }

    @Test
    public void testReal()  {
        String inputCode = """
                protected AxisState drawLabel(String label, Graphics2D g2,\s
                        Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge,\s
                        AxisState state, PlotRenderingInfo plotState) {
                
                    // it is unlikely that 'state' will be null, but check anyway...
                    if (state == null) {
                        throw new IllegalArgumentException("Null 'state' argument.");
                    }
                   \s
                    if ((label == null) || (label.equals(""))) {
                        return state;
                    }
                
                    Font font = getLabelFont();
                    RectangleInsets insets = getLabelInsets();
                    g2.setFont(font);
                    g2.setPaint(getLabelPaint());
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, fm);
                    Shape hotspot = null;
                   \s
                    if (edge == RectangleEdge.TOP) {
                        AffineTransform t = AffineTransform.getRotateInstance(
                                getLabelAngle(), labelBounds.getCenterX(),\s
                                labelBounds.getCenterY());
                        Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                        labelBounds = rotatedLabelBounds.getBounds2D();
                        float w = (float) labelBounds.getWidth();
                        float h = (float) labelBounds.getHeight();
                        float labelx = (float) dataArea.getCenterX();
                        float labely = (float) (state.getCursor() - insets.getBottom()\s
                                - h / 2.0);
                        TextUtilities.drawRotatedString(label, g2, labelx, labely,\s
                                TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
                        hotspot = new Rectangle2D.Float(labelx - w / 2.0f,\s
                                labely - h / 2.0f, w, h);
                        state.cursorUp(insets.getTop() + labelBounds.getHeight()\s
                                + insets.getBottom());
                    }
                    else if (edge == RectangleEdge.BOTTOM) {
                        AffineTransform t = AffineTransform.getRotateInstance(
                                getLabelAngle(), labelBounds.getCenterX(),\s
                                labelBounds.getCenterY());
                        Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                        labelBounds = rotatedLabelBounds.getBounds2D();
                        float w = (float) labelBounds.getWidth();
                        float h = (float) labelBounds.getHeight();
                        float labelx = (float) dataArea.getCenterX();
                        float labely = (float) (state.getCursor() + insets.getTop()\s
                                + h / 2.0);
                        TextUtilities.drawRotatedString(label, g2, labelx, labely,\s
                                TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
                        hotspot = new Rectangle2D.Float(labelx - w / 2.0f,\s
                                labely - h / 2.0f, w, h);
                        state.cursorDown(insets.getTop() + labelBounds.getHeight()\s
                                + insets.getBottom());
                    }
                    else if (edge == RectangleEdge.LEFT) {
                        AffineTransform t = AffineTransform.getRotateInstance(
                                getLabelAngle() - Math.PI / 2.0, labelBounds.getCenterX(),\s
                                labelBounds.getCenterY());
                        Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                        labelBounds = rotatedLabelBounds.getBounds2D();
                        float w = (float) labelBounds.getWidth();
                        float h = (float) labelBounds.getHeight();
                        float labelx = (float) (state.getCursor() - insets.getRight()\s
                                - w / 2.0);
                        float labely = (float) dataArea.getCenterY();
                        TextUtilities.drawRotatedString(label, g2, labelx, labely,\s
                                TextAnchor.CENTER, getLabelAngle() - Math.PI / 2.0,\s
                                TextAnchor.CENTER);
                        hotspot = new Rectangle2D.Float(labelx - w / 2.0f,\s
                                labely - h / 2.0f, w, h);
                        state.cursorLeft(insets.getLeft() + labelBounds.getWidth()\s
                                + insets.getRight());
                    }
                    else if (edge == RectangleEdge.RIGHT) {
                
                        AffineTransform t = AffineTransform.getRotateInstance(
                                getLabelAngle() + Math.PI / 2.0,\s
                                labelBounds.getCenterX(), labelBounds.getCenterY());
                        Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                        labelBounds = rotatedLabelBounds.getBounds2D();
                        float w = (float) labelBounds.getWidth();
                        float h = (float) labelBounds.getHeight();
                        float labelx = (float) (state.getCursor()\s
                                        + insets.getLeft() + w / 2.0);
                        float labely = (float) (dataArea.getY() + dataArea.getHeight()\s
                                / 2.0);
                        TextUtilities.drawRotatedString(label, g2, labelx, labely,\s
                                TextAnchor.CENTER, getLabelAngle() + Math.PI / 2.0,\s
                                TextAnchor.CENTER);
                        hotspot = new Rectangle2D.Float(labelx - w / 2.0f,\s
                                labely - h / 2.0f, w, h);
                        state.cursorRight(insets.getLeft() + labelBounds.getWidth()\s
                                + insets.getRight());
                
                    }
                    if (plotState != null && hotspot != null) {
                        ChartRenderingInfo owner = plotState.getOwner();
                            EntityCollection entities = owner.getEntityCollection();
                            if (entities != null) {
                                entities.add(new AxisLabelEntity(this, hotspot,\s
                                        this.labelToolTip, this.labelURL));
                            }
                    }
                    return state;
                
                }""";
        String expectedCode = """
                protected AxisState drawLabel(
                      String label,
                      Graphics2D g2,
                      Rectangle2D plotArea,
                      Rectangle2D dataArea,
                      RectangleEdge edge,
                      AxisState state,
                      PlotRenderingInfo plotState) {
                    if (state == null) {
                      throw new IllegalArgumentException("Null 'state' argument.");
                    }
                    if ((label == null) || (label.equals(""))) {
                      return state;
                    }
                    Font font = getLabelFont();
                    RectangleInsets insets = getLabelInsets();
                    g2.setFont(font);
                    g2.setPaint(getLabelPaint());
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, fm);
                    Shape hotspot = null;
                    if (edge == RectangleEdge.TOP) {
                      AffineTransform t =
                          AffineTransform.getRotateInstance(
                              getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
                      Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                      labelBounds = rotatedLabelBounds.getBounds2D();
                      float w = (float) labelBounds.getWidth();
                      float h = (float) labelBounds.getHeight();
                      float labelx = (float) dataArea.getCenterX();
                      float labely = (float) (state.getCursor() - insets.getBottom() - h / 2.0);
                      TextUtilities.drawRotatedString(
                          label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
                      hotspot = new Rectangle2D.Float(labelx - w / 2.0f, labely - h / 2.0f, w, h);
                      state.cursorUp(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
                    } else {
                      if (edge == RectangleEdge.BOTTOM) {
                        AffineTransform t =
                            AffineTransform.getRotateInstance(
                                getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
                        Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                        labelBounds = rotatedLabelBounds.getBounds2D();
                        float w = (float) labelBounds.getWidth();
                        float h = (float) labelBounds.getHeight();
                        float labelx = (float) dataArea.getCenterX();
                        float labely = (float) (state.getCursor() + insets.getTop() + h / 2.0);
                        TextUtilities.drawRotatedString(
                            label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
                        hotspot = new Rectangle2D.Float(labelx - w / 2.0f, labely - h / 2.0f, w, h);
                        state.cursorDown(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
                      } else {
                        if (edge == RectangleEdge.LEFT) {
                          AffineTransform t =
                              AffineTransform.getRotateInstance(
                                  getLabelAngle() - Math.PI / 2.0,
                                  labelBounds.getCenterX(),
                                  labelBounds.getCenterY());
                          Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                          labelBounds = rotatedLabelBounds.getBounds2D();
                          float w = (float) labelBounds.getWidth();
                          float h = (float) labelBounds.getHeight();
                          float labelx = (float) (state.getCursor() - insets.getRight() - w / 2.0);
                          float labely = (float) dataArea.getCenterY();
                          TextUtilities.drawRotatedString(
                              label,
                              g2,
                              labelx,
                              labely,
                              TextAnchor.CENTER,
                              getLabelAngle() - Math.PI / 2.0,
                              TextAnchor.CENTER);
                          hotspot = new Rectangle2D.Float(labelx - w / 2.0f, labely - h / 2.0f, w, h);
                          state.cursorLeft(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
                        } else {
                          if (edge == RectangleEdge.RIGHT) {
                            AffineTransform t =
                                AffineTransform.getRotateInstance(
                                    getLabelAngle() + Math.PI / 2.0,
                                    labelBounds.getCenterX(),
                                    labelBounds.getCenterY());
                            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
                            labelBounds = rotatedLabelBounds.getBounds2D();
                            float w = (float) labelBounds.getWidth();
                            float h = (float) labelBounds.getHeight();
                            float labelx = (float) (state.getCursor() + insets.getLeft() + w / 2.0);
                            float labely = (float) (dataArea.getY() + dataArea.getHeight() / 2.0);
                            TextUtilities.drawRotatedString(
                                label,
                                g2,
                                labelx,
                                labely,
                                TextAnchor.CENTER,
                                getLabelAngle() + Math.PI / 2.0,
                                TextAnchor.CENTER);
                            hotspot = new Rectangle2D.Float(labelx - w / 2.0f, labely - h / 2.0f, w, h);
                            state.cursorRight(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
                          }
                        }
                      }
                    }
                    if (plotState != null && hotspot != null) {
                      ChartRenderingInfo owner = plotState.getOwner();
                      EntityCollection entities = owner.getEntityCollection();
                      if (entities != null) {
                        entities.add(new AxisLabelEntity(this, hotspot, this.labelToolTip, this.labelURL));
                      }
                    }
                    return state;
                  }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(3), snippet.getTransformations().get(Transformation.NestElseIf));
    }

    @Test
    public void testMultiple()  {
        String inputCode = """
                public int test() {
                  if (condition1()) {
                    return 1;
                  } else if (condition2()) {
                    return 2;
                  } else if (condition3()) {
                    return 3;
                  }
                }""";
        String expectedCode = """
                public int test() {
                  if (condition1()) {
                    return 1;
                  } else {
                    if (condition2()) {
                      return 2;
                    } else {
                      if (condition3()) {
                        return 3;
                      }
                    }
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.NestElseIf));
    }


}

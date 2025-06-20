package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SwapEqualsOperandsTransformerTest {

    Transformer transformer = new SwapEqualsOperandsTransformer();

    @Test
    public void testEqual() {
        String inputCode = """
                public boolean test(int i) {
                  return i == 1;
                }""";
        String expectedCode = """
                public boolean test(int i) {
                  return 1 == i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.SwapEqualsOperands));
    }

    @Test
    public void testNotEqual() {
        String inputCode = """
                public boolean test(int i) {
                  return i != 1;
                }""";
        String expectedCode = """
                public boolean test(int i) {
                  return 1 != i;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.SwapEqualsOperands));
    }

    @Test
    public void testNested() {
        String inputCode = """
                public boolean test(int i) {
                  return (i == 1) == true;
                }""";
        String expectedCode = """
                public boolean test(int i) {
                  return true == (1 == i);
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.SwapEqualsOperands));
    }

    @Test
    public void testAssignmentInCondition() {
        String inputCode = """
                public boolean test(int i) {
                  return ((i = 1) + 1) == true;
                }""";
        String expectedCode = """
                public boolean test(int i) {
                  return ((i = 1) + 1) == true;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.SwapEqualsOperands));
    }

    @Test
    public void testReal() {
        String inputCode = """
                public LegendItemCollection getLegendItems() {
                    LegendItemCollection result = new LegendItemCollection();
                    if (this.plot == null) {
                        return result;
                    }
                    int index = this.plot.getIndexOf(this);
                    CategoryDataset dataset = this.plot.getDataset(index);
                    if (dataset != null) {
                        return result;
                    }
                    int seriesCount = dataset.getRowCount();
                    if (plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
                        for (int i = 0; i < seriesCount; i++) {
                            if (isSeriesVisibleInLegend(i)) {
                                LegendItem item = getLegendItem(index, i);
                                if (item != null) {
                                    result.add(item);
                                }
                            }
                        }
                    }
                    else {
                        for (int i = seriesCount - 1; i >= 0; i--) {
                            if (isSeriesVisibleInLegend(i)) {
                                LegendItem item = getLegendItem(index, i);
                                if (item != null) {
                                    result.add(item);
                                }
                            }
                        }
                    }
                    return result;
                }""";
        String expectedCode = """
                public LegendItemCollection getLegendItems() {
                    LegendItemCollection result = new LegendItemCollection();
                    if (null == this.plot) {
                        return result;
                    }
                    int index = this.plot.getIndexOf(this);
                    CategoryDataset dataset = this.plot.getDataset(index);
                    if (null != dataset) {
                        return result;
                    }
                    int seriesCount = dataset.getRowCount();
                    if (plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
                        for (int i = 0; i < seriesCount; i++) {
                            if (isSeriesVisibleInLegend(i)) {
                                LegendItem item = getLegendItem(index, i);
                                if (null != item) {
                                    result.add(item);
                                }
                            }
                        }
                    }
                    else {
                        for (int i = seriesCount - 1; i >= 0; i--) {
                            if (isSeriesVisibleInLegend(i)) {
                                LegendItem item = getLegendItem(index, i);
                                if (null != item) {
                                    result.add(item);
                                }
                            }
                        }
                    }
                    return result;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(4), snippet.getTransformations().get(Transformation.SwapEqualsOperands));
    }
}

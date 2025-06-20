package org.jetbrains.research.codecocoon.transformer;

import org.jetbrains.research.codecocoon.Snippet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SwapRelationOperandsTransformerTest {

    Transformer transformer = new SwapRelationOperandsTransformer();

    @Test
    public void testGreater() {
        String inputCode = """
                public int test(int i) {
                  if (i > 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (0 < i) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.SwapRelationOperands));
    }

    @Test
    public void testLess() {
        String inputCode = """
                public int test(int i) {
                  if (i < 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (0 > i) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.SwapRelationOperands));
    }

    @Test
    public void testGreaterEquals() {
        String inputCode = """
                public int test(int i) {
                  if (i >= 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (0 <= i) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testLessEquals() {
        String inputCode = """
                public int test(int i) {
                  if (i >= 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (0 <= i) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void testNested() {
        String inputCode = """
                public int test(int i) {
                  if ((1 ? i < 16 : 0) > 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (0 < (1 ? 16 > i : 0)) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.SwapRelationOperands));
    }

    @Test
    public void testAssignmentInCondition() {
        String inputCode = """
                public boolean test(int i) {
                  return ((i = 1) + 1) > 5;
                }""";
        String expectedCode = """
                public boolean test(int i) {
                  return ((i = 1) + 1) > 5;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.SwapRelationOperands));
    }


    @Test
    public void testNotApplicable() {
        String inputCode = """
                public int test(int i) {
                  if (i == 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        String expectedCode = """
                public int test(int i) {
                  if (i == 0) {
                    return i;
                  } else {
                    return 0;
                  }
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.SwapRelationOperands));
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
                        for (int i = 0; seriesCount > i; i++) {
                            if (isSeriesVisibleInLegend(i)) {
                                LegendItem item = getLegendItem(index, i);
                                if (item != null) {
                                    result.add(item);
                                }
                            }
                        }
                    }
                    else {
                        for (int i = seriesCount - 1; 0 <= i; i--) {
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
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.SwapRelationOperands));
    }
}

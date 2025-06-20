package org.jetbrains.research.codecocoon.transformer.identifiers;

import org.jetbrains.research.codecocoon.Snippet;
import org.jetbrains.research.codecocoon.synonyms.SynonymGenerator;
import org.jetbrains.research.codecocoon.transformer.SnippetWrapper;
import org.jetbrains.research.codecocoon.transformer.Transformation;
import org.jetbrains.research.codecocoon.transformer.Transformer;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VariableNameTransformerTest {

    SynonymGenerator mockSynonymGenerator = Mockito.mock(SynonymGenerator.class);
    Transformer transformer = new VariableNameTransformer(mockSynonymGenerator);

    @Test
    public void testSimple() {
        String inputCode = """
                public int test() {
                  int value = 1;
                  return value;
                }""";
        String expectedCode = """
                public int test() {
                  int result = 1;
                  return result;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("value", inputCode, "variable")).thenReturn("result");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameVariables));
    }

    @Test
    public void testNested() {
        String inputCode = """
                public int sum(int[] values) {
                  int sum = 0;
                  if (sum == 0) {
                    int value = 1;
                    sum = value;
                  }
                  return sum;
                }""";
        String expectedCode = """
                public int sum(int[] values) {
                  int result = 0;
                  if (result == 0) {
                    int increment = 1;
                    result = increment;
                  }
                  return result;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("sum", inputCode, "variable")).thenReturn("result");
        when(mockSynonymGenerator.generateSynonymFor("value", inputCode, "variable")).thenReturn("increment");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(2), snippet.getTransformations().get(Transformation.RenameVariables));
    }

    @Test
    public void testReservedVariableName() {
        String inputCode = """
                public int test() {
                  int reserved = 1;
                  return reserved;
                }""";
        String expectedCode = """
                public int test() {
                  int reserved = 1;
                  return reserved;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("reserved", inputCode, "variable")).thenReturn("void");

        transformer.transform(snippet);

        verify(mockSynonymGenerator).generateSynonymFor("reserved", inputCode, "variable");
        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameVariables));
    }

    @Test
    public void testillegalVariableName() {
        String inputCode = """
                public int test() {
                  int illegal = 1;
                  return illegal;
                }""";
        String expectedCode = """
                public int test() {
                  int illegal = 1;
                  return illegal;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("illegal", inputCode, "variable")).thenReturn("-a");

        transformer.transform(snippet);

        verify(mockSynonymGenerator).generateSynonymFor("illegal", inputCode, "variable");
        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameVariables));
    }

    @Test
    public void testVariableNameCollision1() {
        String inputCode = """
                public int test() {
                  int dataset = 1;
                  int data = 1;
                  return data;
                }""";
        String expectedCode = """
                public int test() {
                  int dataset = 1;
                  int data = 1;
                  return data;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("dataset", inputCode, "variable")).thenReturn("data");
        when(mockSynonymGenerator.generateSynonymFor("data", inputCode, "variable")).thenReturn("data");

        transformer.transform(snippet);

        verify(mockSynonymGenerator).generateSynonymFor("dataset", inputCode, "variable");
        verify(mockSynonymGenerator).generateSynonymFor("data", inputCode, "variable");
        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameVariables));
    }

    @Test
    public void testVariableNameCollision2() {
        String inputCode = """
                public int test() {
                  int dataset1 = 1;
                  int dataset2 = 1;
                  return dataset1;
                }""";
        String expectedCode = """
                public int test() {
                  int data = 1;
                  int dataset2 = 1;
                  return data;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("dataset1", inputCode, "variable")).thenReturn("data");
        when(mockSynonymGenerator.generateSynonymFor("dataset2", inputCode, "variable")).thenReturn("data");

        transformer.transform(snippet);

        verify(mockSynonymGenerator).generateSynonymFor("dataset1", inputCode, "variable");
        verify(mockSynonymGenerator).generateSynonymFor("dataset2", inputCode, "variable");
        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(1), snippet.getTransformations().get(Transformation.RenameVariables));
    }

    @Test
    public void testVariableNameCollisionWithParameter() {
        String inputCode = """
                public int test(int b) {
                  int a = 1;
                  return b;
                }""";
        String expectedCode = """
                public int test(int b) {
                  int a = 1;
                  return b;
                }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("a", inputCode, "variable")).thenReturn("b");

        transformer.transform(snippet);

        verify(mockSynonymGenerator).generateSynonymFor("a", inputCode, "variable");
        assertEquals(expectedSnippet, snippet);
        assertFalse(snippet.getTransformations().containsKey(Transformation.RenameVariables));
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
                    LegendItemCollection answer = new LegendItemCollection();
                    if (this.plot == null) {
                      return answer;
                    }
                    int index = this.plot.getIndexOf(this);
                    CategoryDataset data = this.plot.getDataset(index);
                    if (data != null) {
                      return answer;
                    }
                    int seriesCounter = data.getRowCount();
                    if (plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
                      for (int i = 0; i < seriesCounter; i++) {
                        if (isSeriesVisibleInLegend(i)) {
                          LegendItem legendItem = getLegendItem(index, i);
                          if (legendItem != null) {
                            answer.add(legendItem);
                          }
                        }
                      }
                    } else {
                      for (int i = seriesCounter - 1; i >= 0; i--) {
                        if (isSeriesVisibleInLegend(i)) {
                          LegendItem legendItem = getLegendItem(index, i);
                          if (legendItem != null) {
                            answer.add(legendItem);
                          }
                        }
                      }
                    }
                    return answer;
                  }""";
        Snippet snippet = SnippetWrapper.wrap(inputCode);
        Snippet expectedSnippet = SnippetWrapper.wrap(expectedCode);

        when(mockSynonymGenerator.generateSynonymFor("result", inputCode, "variable")).thenReturn("answer");
        when(mockSynonymGenerator.generateSynonymFor("dataset", inputCode, "variable")).thenReturn("data");
        when(mockSynonymGenerator.generateSynonymFor("seriesCount", inputCode, "variable")).thenReturn("seriesCounter");
        when(mockSynonymGenerator.generateSynonymFor("item", inputCode, "variable")).thenReturn("legendItem");

        transformer.transform(snippet);

        assertEquals(expectedSnippet, snippet);
        assertEquals(Integer.valueOf(4), snippet.getTransformations().get(Transformation.RenameVariables));
    }

    @Test
    public void testEquals() {
        SynonymGenerator synonymGenerator = Mockito.mock(SynonymGenerator.class);
        Transformer variableNameTransformer1 = new VariableNameTransformer(synonymGenerator);
        Transformer variableNameTransformer2 = new VariableNameTransformer(synonymGenerator);

        assertEquals(variableNameTransformer1, variableNameTransformer2);
    }

    @Test
    public void testNotEquals() {
        SynonymGenerator synonymGenerator1 = Mockito.mock(SynonymGenerator.class);
        SynonymGenerator synonymGenerator2 = Mockito.mock(SynonymGenerator.class);
        Transformer variableNameTransformer1 = new VariableNameTransformer(synonymGenerator1);
        Transformer variableNameTransformer2 = new VariableNameTransformer(synonymGenerator2);

        assertNotEquals(variableNameTransformer1, variableNameTransformer2);
    }
}


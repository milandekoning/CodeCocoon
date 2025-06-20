package org.jetbrains.research.codecocoon.synonyms;

import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.JWNLException;

public class LexicalSynonymGenerator implements SynonymGenerator {

    private final Dictionary dictionary;

    public LexicalSynonymGenerator() throws JWNLException {
        this.dictionary = Dictionary.getDefaultResourceInstance();
    }

    @Override
    public String generateSynonymFor(String identifierName, String context, String identifierType){
        if (identifierName.length() == 1) return identifierName;

        try {
            IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, identifierName);

            if (indexWord == null) return identifierName;

            for (Synset synset : indexWord.getSenses()) {
                for (Word synWord : synset.getWords()) {
                    // Sometimes, synonyms are multiple words.
                    String synonym = synWord.getLemma().split(" ")[0];

                    if (!synonym.equals(identifierName)) {
                        return synonym;
                    }
                }
            }
            return identifierName;
        } catch(JWNLException e) {
            return identifierName;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LexicalSynonymGenerator;
    }
}

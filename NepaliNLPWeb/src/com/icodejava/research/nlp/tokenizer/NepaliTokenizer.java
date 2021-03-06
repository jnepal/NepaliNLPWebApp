package com.icodejava.research.nlp.tokenizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.icodejava.research.nlp.domain.Grammar;
import com.icodejava.research.nlp.domain.WordFrequency;


public class NepaliTokenizer {

    public static void main(String args[]) throws FileNotFoundException, IOException {

        //String text = new String(FileUtilities.loadFile("C:\\Users\\paudyals\\Desktop\\NLP\\nepali_sambidhan.txt"));
        //String text = ArticlesDB.selectArticleTextByID(7000);
    	//String text = HtmlTextExtractor.extractTextFromWeb("http://www.hongkongnepali.com/2008-12-29-07-00-23/9088-2011-10-04-17-26-34.html");
        //tokenizeSentence(text, Terminator.NP);
        //tokenizeWords(text);
        //extractAdjancentWords(AdjacentWords.THREE_WORDS.getDepth(),"This is a test sentence really good one");
        //getWordFrequencyMap(text);
    }

    /**
     * This method takes a big text and returns a list of sentences.
     *
     * @param text
     * @param langTerminator
     */
    public static List<String> tokenizeSentence(String text, Terminator langTerminator) {

        List<String> sentences = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(text,langTerminator.getSentenceTerminator());

        while (tokenizer.hasMoreElements()) {

            String token = cleanSentence(tokenizer.nextToken());

            if (token.trim().length() > 0) {
            	
                sentences.add(token.trim());
                System.out.println(token.trim());

                // extract the n-level word combination
                //extractAdjancentWords(AdjacentWords.THREE_WORDS.getDepth(), token.trim()); // TODO: Remove or improve
            }
        }
        
        return sentences;
    }

    /**
     * This method takes a larger text and gives a list of words in it.
     * 
     * The words are cleaned and sanitized.
     * @param text
     * @return
     */
    public static List<String> tokenizeWords(String text) {

        List<String> words = new ArrayList<String>();

        if (text == null) {
            return words;
        }

        StringTokenizer tokenizer = new StringTokenizer(text);

        while (tokenizer.hasMoreElements()) {

            String token = cleanWordToken(tokenizer.nextToken());

            if (token.length() > 0) {
                words.add(token);
            }
        }

        return words;
    }
    


    public static String cleanWordToken(String word) {

        word = word.trim();
        word = word.replace("\u00A0",""); //remove non breaking spaces
        word = word.trim().replaceAll("( )+", " ");//remove multiple spaces
        word = word.replaceAll("[\t००–-—-�•�…_a-zA-Z0-9@#&\\$%:,;=->~“”‘’!। ||/\\+\\^\\*\\'\"\\.`\\(\\)\\[\\]\\{\\}\\.\\?\\\\]*", "");
        word = word.replaceAll("¥", "र्");
        word = word.replaceAll("©", "");
        word = word.replaceAll(">>", "");
        
		if ("÷".equalsIgnoreCase(word)) {
			word = "";
		} else if (word.indexOf("÷") == 0) {
			word = word.substring(1);//e.g. ÷अनौपचारिक > अनौपचारिक 
		} else {
			word = word.replaceAll("÷", "/"); //e.g. औपचारिक÷अनौपचारिक > औपचारिक/अनौपचारिक

		}
        
        word = fixMalformedWord(word);

        return word;
    }
    
    public static String cleanSentence(String sentence) {
    	
    	if(sentence ==  null) {
    		return null;
    	}

        sentence = sentence.trim();
        sentence = sentence.replace("\u00A0",""); //remove non breaking spaces
        sentence = sentence.trim().replaceAll("( )+", " ");//remove multiple spaces
        
        //sentence = sentence.replaceAll("  ", " ").replaceAll("  ", " ");
        sentence = sentence.replaceAll("[a-zA-Z0-9][,.%;:]{0,}", "");//remove english characters. Removes comma appearing with english text, but not nepali text
        sentence = sentence.replaceAll("\t", " ");
        sentence = sentence.replaceAll("#", "");
        sentence = sentence.replaceAll("@", "");
        sentence = sentence.replaceAll("\"", "");
        sentence = sentence.replaceAll("\'", "");
        sentence = sentence.replaceAll("©", "");
        sentence = sentence.replaceAll(">>", "");
        sentence = sentence.trim().replaceAll("( )+", " ");//remove multiple spaces
        sentence = sentence.trim();
        
        return sentence;
    }
    
	public static String cleanTitles(String title) {
		title = title.trim();
		//title = title.replaceAll("  ", " ").replaceAll(" ", ""); //do it twice
		title = title.replaceAll("[००–-—-�•�…_a-zA-Z0-9@#&\\$%||/\\+\\^\\*\\.]*", "");
		title=title.trim();
		return title;
	}



    public static Map<String,Integer> getWordFrequencyMap(String text) {
            List<String> words = tokenizeWords(text);
            Map<String, Integer> frequencyMap = new HashMap<String, Integer>();

            for (String word : words) {
                if(frequencyMap.get(word) != null) {
                    Integer frequency = frequencyMap.get(word);
                    frequency = frequency + 1;

                    frequencyMap.put(word, frequency);
                } else {
                    frequencyMap.put(word, new Integer(1));
                }
            }

            printSortedMap(frequencyMap);

            return frequencyMap;
    }

    private static void printSortedMap(Map<String, Integer> frequencyMap) {
    	

        Set<String> words =  frequencyMap.keySet();

        System.out.println("Total Words Found: " + words.size());

        List<WordFrequency> wordsList = new ArrayList<WordFrequency>();

        for(String word:words) {
            wordsList.add(new WordFrequency(word, frequencyMap.get(word)));
        }

        //Collections.sort(wordsList, new WordFrequency<T>());

        Collections.sort(wordsList, new Comparator<WordFrequency>(){
            public int compare(WordFrequency w1, WordFrequency w2) {
                return w2.getFrequency().compareTo(w1.getFrequency());  //reverse order
            }
        });

        System.out.println(wordsList);


    }

    public static enum Terminator {
        NP("।?!|"), EN(".");

        private String sentenceTerminator;

        Terminator(String sentenceTerminator) {

            this.sentenceTerminator = sentenceTerminator;
        }

        public String getSentenceTerminator() {

            return sentenceTerminator;
        }

    };

    public static void printChracters (String str) {
    	if(str == null) {
    		return;
    	}
    	
    	for(Character c: str.toCharArray()) {
    		System.out.println((c + " " + (int) c));
    	}
    }


	
	public static String fixMalformedWord(String string) {
		//SET_OF_MATRAS = "ा ि ी ु ू ृ े ै ो ौ ं : ँ ॅ्" 
		//NepaliTokenizer.printChracters(string);
		
		if(string.indexOf('ि') == 0 && string.length() > 1) {
			System.out.println("Fixing: " + string);
			string=swap(string, 0,1);
			System.out.println("Fixed: " + string);
		}
		
		string = string.replaceAll("अो", "ओ");
		string = string.replaceAll("अा", "आ");
		string = string.replaceAll((char)2366 +""+ (char)2375, "\u094B");//छाेपे  -> छोपे
		string = string.replaceAll("\u093E" + "" + "\u093E", "\u093E");//टीभीमाा -> टीभीमा
		string = string.replaceAll("\u094D" + "\u094C" , "\u094C") ; //सम्झ्ौता -> सम्झौता
		string = string.replaceAll("\u094D" + "\u093E", "\u093E");//प्ााण्डेले -> पाण्डेले
		string = string.replaceAll("\u0941"+ "\u0941", "\u0941");//दुुर्घटनामा -> दुर्घटनामा
		
		return string;
	}
	
	/**
	 * Swaps two characters in a given string. 
	 * @param String
	 * @param firstIndex
	 * @param secondIndex
	 * @return
	 */
	public static String swap(String s, int firstIndex, int secondIndex) {
		String s1 = s.substring(0, firstIndex);
		String s2 = s.substring(firstIndex + 1, secondIndex);
		String s3 = s.substring(secondIndex + 1);
		return s1 + s.charAt(secondIndex) + s2 + s.charAt(firstIndex) + s3;
	}

	/**
	 * Detects Grammatically incorrect Nepali Words
	 * @param string
	 * @return
	 * 
	 * Currently cant detect: अाऊन
	 */
	public static boolean isMalformedWord(String string) {

		int malformedCount = 0;
		boolean isMalformed = false;
		for(Character c: string.toCharArray()) {
			
			if(Grammar.isMatra(c)) {
				malformedCount+=1;
			} else {
				malformedCount=0; //reset
			}
			
			if(malformedCount>=2) {
				isMalformed = true;
				break;
			}
		}
		System.out.println("isMalformed? " + isMalformed);
		return isMalformed;
		
	}

}
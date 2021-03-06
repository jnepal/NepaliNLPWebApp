package com.icodejava.research.nlp.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.icodejava.research.nlp.database.ArticlesDB;
import com.icodejava.research.nlp.database.Tables;
import com.icodejava.research.nlp.database.WordsUnreferencedDB;
import com.icodejava.research.nlp.domain.CompoundWordEnding;
import com.icodejava.research.nlp.domain.NameWordEnding;
import com.icodejava.research.nlp.domain.Word;
import com.icodejava.research.nlp.stemmer.NepaliStemmer;
import com.icodejava.research.nlp.tokenizer.NepaliTokenizer;
import com.icodejava.research.nlp.utils.DevanagariUnicodeToRomanEnglish;

public class WordsUnreferencedService {

	public static final int HOW_MANY_WORDS = 1000000;

	public static void main(String args[]) throws InterruptedException {
		// printCompoundWords();
		// printCompoundWordsNotTagged();
		// romanizeAndSaveWords(HOW_MANY_WORDS_TO_ROMANIZE);
		// WordsUnreferencedDB.selectCompoundWordsNotTagged("मुन्तिर");
		// WordsUnreferencedDB.selectCompoundWordsNotTagged("मै");

		//extractAndTagRootWords(); // Multiple

		 //tagCompoundWords(HOW_MANY_WORDS);
		//processUnreferencedWords(10000);
		// processWordsFromFile("src/com/icodejava/research/nlp/sources/other/misc_words.txt");
		// getRomanizedWordCount();
		// removeDuplication();
		 
		// getRandomCompoundWords(1000);
		 
		 //updateWordRootFromKnownRoots();
		 //updateCompoundWordRootsFromKnownRoots(); //TODO: NEEDS OPTIMIZATION
	    
	}

	public static List<Word> getRandomCompoundWords(int limit) {
		List<Word> words = WordsUnreferencedDB.selectRandomCompoundWords(limit);
		
		for(Word word:words) {
			word.setRootWord(NepaliStemmer.getNepaliRootWord(word.getWord()));
			//System.out.println("Word:" + word.getWord() + " Root: " + word.getRootWord());
		}
		
		return words;
		
	}
	
	public static List<Word>selectRootWordPOSNotExtracted(int limit) {
		return WordsUnreferencedDB.selectRootWordPOSNotExtracted(limit);
	}

	public static int getRomanizedWordCount() {
		int count = WordsUnreferencedDB.selectRomanizedWordsCount();
		System.out.println("Romanized Word Count: " + count);
		return count;
		
	}

	public static void removeDuplication() throws InterruptedException {
		WordsUnreferencedDB.removeDuplicateWords();
		
	}

	/**
	 * Given a compound word ending, it finds out all the words that end with
	 * that compound word ending but not already tagged as extracted.
	 * 
	 * @param compoundWordEnding
	 */
	private static void extractAndTagRootWords(String compoundWordEnding) {
		
		  List<Word> words = WordsUnreferencedDB.selectCompoundWordsRootWordsNotExtracted(compoundWordEnding);
		  for(Word word:words) {
			  word.setRootWord(NepaliStemmer.getNepaliRootWord(word.getWord()));
			  word.setIsRootWordExtracted("Y");
			  //System.out.println(word.getWord() +"-->" + word.getRootWord());
		  }
		  
		  WordsUnreferencedDB.updateWordTagRootWords(words);
		
	}
	
	/**
	 * Using a list of defined Compound Word Endings, this finds out all the words that end with
	 * those compound word endings but not already tagged as extracted.
	 * 
	 * @param compoundWordEnding
	 */
	private static void extractAndTagRootWords() {
		
		List<String> cwes =	Arrays.asList(new String []{
				  CompoundWordEnding.BHANDAA.getNepaliWordEnding(),
				  CompoundWordEnding.LAGAYAT.getNepaliWordEnding(),
				  CompoundWordEnding.LAGAYATKA.getNepaliWordEnding(),
				  CompoundWordEnding.LAGAYATKO.getNepaliWordEnding(),
				  CompoundWordEnding.PURNA_1.getNepaliWordEnding(),
				  CompoundWordEnding.PURNA_2.getNepaliWordEnding(),
				  CompoundWordEnding.PASCHAT_1.getNepaliWordEnding(),
				  CompoundWordEnding.PASCHAT_2.getNepaliWordEnding(),
				  CompoundWordEnding.PASCHAT_3.getNepaliWordEnding(),
				  CompoundWordEnding.HARU_SANGA_1.getNepaliWordEnding(),
				  CompoundWordEnding.HARU_SANGA_2.getNepaliWordEnding(),
				  CompoundWordEnding.HARU_SANGA_3.getNepaliWordEnding(), 
				  CompoundWordEnding.BARE.getNepaliWordEnding(), 
				  CompoundWordEnding.SAKEKA.getNepaliWordEnding(),
				  CompoundWordEnding.SAKEKO.getNepaliWordEnding(),
				  CompoundWordEnding.HARUSAMMA_1.getNepaliWordEnding(),
				  CompoundWordEnding.HARUSAMMA_2.getNepaliWordEnding(),
				  CompoundWordEnding.BHITRAI.getNepaliWordEnding(),
				  CompoundWordEnding.BHITRA.getNepaliWordEnding(),
				  CompoundWordEnding.BHITRAKA.getNepaliWordEnding(),
				  CompoundWordEnding.BHITRAMA.getNepaliWordEnding(), 
				  CompoundWordEnding.HARUKO_1.getNepaliWordEnding(),
				  CompoundWordEnding.HARUKO_2.getNepaliWordEnding(),
				  CompoundWordEnding.BATA.getNepaliWordEnding(),
				  CompoundWordEnding.BATAI.getNepaliWordEnding(),
				  CompoundWordEnding.LAEE.getNepaliWordEnding(),
				  
				  });
		
		for(String compoundWordEnding: cwes) {
			extractAndTagRootWords(compoundWordEnding);
		}
	}
	

	/**
	 * This method fetches compound words that are not already marked as compound and tags them as compound.
	 * 
	 * @param limit --> How many words to fetch from the database
	 */
	public static void tagCompoundWords(int limit) {
		List<Word> words = WordsUnreferencedDB.selectRecordsNotMarkedAsCompound(limit);
		
		
		//Tag as Compound Words
		for(Word word: words) {
			String value = word.getWord();
			
			 if(endsWithCompoundWord(value)) {
				word.setIsCompoundWord("Y");
			}
			
		} 
		
		//Then update the database
		WordsUnreferencedDB.updateWordsMarkAsCompound(words);
			
		
	}
	
	

	private static boolean endsWithCompoundWord(String word) {
		boolean shouldProcess = false;
		for(CompoundWordEnding cwe: CompoundWordEnding.values()) {
			if(word.endsWith(cwe.getNepaliWordEnding()) && word.length() > cwe.getNepaliWordEnding().length() /*&& !isExceptionCompoundWordEnding(cwe)*/) {
				shouldProcess = true;
			}

		}

		return shouldProcess;
	}

	/**
	 * There shorter compound word endings need to be carefully analyzed due to
	 * the volume of word they impact, so putting an exception right now before
	 * tagging the compound word.
	 * 
	 * @param cwe
	 * @return
	 */
	private static boolean isExceptionCompoundWordEnding(CompoundWordEnding cwe) {
		
		return cwe == cwe.KA || 
				cwe ==cwe.MA ||
				cwe == cwe.KO ||
				cwe == cwe.LE || 
				cwe == cwe.MA ||
				cwe == cwe.KEE;
	}

	public static void romanizeAndSaveWords(int limit) {
		List<Word> words = WordsUnreferencedDB.selectWordsNotRomanized(limit);
		
		for(Word word: words) {
			word.setValueRomanizedISOStandard(DevanagariUnicodeToRomanEnglish.convertWord(word.getWord()));
		}
		
		
	}
	
	public static void romanizeAndSaveWords(List<Word> words) {
		
		for(Word word: words) {
			
			//see if it needs to be deleted
			if(word.isMarkedForDeletion()) {
				WordsUnreferencedDB.deleteRecordsByID(word.getId());
			} else {
			
			
				//save romanization
				if("***SHOULD_ROMANIZE".equalsIgnoreCase(word.getValueRomanizedISOStandard())){
	
					word=WordsUnreferencedDB.selectWordByID(word.getId());
					if(word != null && word.getWord() != null) {
						word.setValueRomanizedISOStandard(DevanagariUnicodeToRomanEnglish.convertWord(word.getWord()));
						
						WordsUnreferencedDB.updateRomanizationISO(word.getId(), word.getValueRomanizedISOStandard());
					}
				}
				
				//save classification. TODO: Merge the methods so there is one update
				
				if(word.isClassified()) {
					WordsUnreferencedDB.updateWordClassification(word);
				}
				
				
				
			}
		}
		
		
	}
	
	

	/**
	 * Prints all compound words.
	 */
	public static void printCompoundWords() {

		for (CompoundWordEnding endsWith : CompoundWordEnding.values()) {
			WordsUnreferencedDB.selectCompoundWords(endsWith.getNepaliWordEnding());
		}

	}
	
	/**
	 * Prints Compound Words that are not already tagged as Compound Words
	 */
	public static void printCompoundWordsNotTagged() {

		System.out.println("====COMPOUND WORDS NOT TAGGED=====");
		for (CompoundWordEnding endsWith : CompoundWordEnding.values()) {
			WordsUnreferencedDB.selectCompoundWordsNotTagged(endsWith.getNepaliWordEnding());
		}

	}
	
	/**
	 * Prints word counts that are already tagged as Compound Words
	 */
	public static void printCompoundWordsNotTaggedCount() {

		System.out.println("====COMPOUND WORDS TAGGED=====");
		System.out.println(WordsUnreferencedDB.selectCompoundWordsTaggedCount());

	}
	
	public static void processUnreferencedWords(int articleLimit) {
		List<Integer> unprocessedArticlesID =  ArticlesDB.selectArticlesUnProcessedWords(articleLimit);

		int processing = 0;
		for(Integer id: unprocessedArticlesID) {
			
			System.out.println("Processing record " + processing++ + " out of " + unprocessedArticlesID.size());
			String articleText = ArticlesDB.selectArticleTextByID(id);
			
			//tokenize 
			List<String> words = NepaliTokenizer.tokenizeWords(articleText);
			
			//then store the words to Database
			for(String word:words) {
				
				if(word.indexOf('â') < 0 && word.indexOf('à') < 0) {
					WordsUnreferencedDB.insertWord(word);
				}
			}
			
			
			WordsUnreferencedDB.getRowCount(WordsUnreferencedDB.DATABASE_URL, Tables.WORDS_UNREFERENCED);
			
			//then mark article as processed.
			ArticlesDB.updateArticleMarkAsProcessedForUnreferenceWord(id);
			
		}
		
		//do a second round of cleaning
		WordsUnreferencedDB.cleanStrangeWords();
		
		//finally print report
		//System.out.println(ReportingService.printUnreferencedWordReport(new StringBuffer()));
		
	}
	
	/**
	 * This method loads independent words from a file system
	 */
	private static void processWordsFromFile(String file) {
		BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String word;

				while ((word = reader.readLine()) != null) {

					word=NepaliTokenizer.cleanWordToken(word);
					WordsUnreferencedDB.insertWord(word);
				}

				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void cleanWordsInDatabase(int startId, int endId) {
		
		WordsUnreferencedDB.cleanWords(startId, endId);
		
	}
	
	public static void updateWordsClassification(List<Word> words) {
		for(Word word: words) {
			WordsUnreferencedDB.updateWordClassification(word);
		}
	}
	
	public static void updatePartsOfSpeech(List<Word> words) {
		for(Word word: words) {
			WordsUnreferencedDB.updatePartOfSpeech(word);
		}
	}
	
	public static void updateWordRoot(List<Word> words) {
		for(Word word: words) {
			WordsUnreferencedDB.updateWordRootWordAndTag(word);
		}
	}

	public static List<Word> searchWords(String searchQuery, String searchType, int limit, String verificationType) {
		System.out.println("SearchType: " + searchType);
		String sqlBase = "SELECT * FROM " + Tables.WORDS_UNREFERENCED + " where word like ";
		String sql = "";
		if ("anywhere".equalsIgnoreCase(searchType)) {
			sql = sqlBase + "\'%" + searchQuery + "%\'";
		} else if ("start".equalsIgnoreCase(searchType)) {
			sql = sqlBase + "\'" + searchQuery + "%\'";
		} else if ("end".equalsIgnoreCase(searchType)) {
			sql = sqlBase + "\'%" + searchQuery + "\'";
		}
		
		String andClause = "AND VERIFIED =\"Y\" ";
		
		if("not_verified".equalsIgnoreCase(verificationType)) {
		    andClause = "AND VERIFIED != \"Y\" ";
		} else if ("any".equalsIgnoreCase(verificationType)) {
		    andClause = "";
		}
		
		sql = sql + andClause + " LIMIT " +  limit;

		//System.out.println(sql);

		return WordsUnreferencedDB.selectWithQuery(sql);
	}

	
	public static int getRootWordExtractionCount() {
		return WordsUnreferencedDB.getVerifiedRootWordExtractionCount();
	}
	
	public static void updateWordRootFromKnownRoots() {
		//find all the distinct root words
		List<String> words = WordsUnreferencedDB.getDistinctRootWords();
		System.out.println("Found: " + words.size() + "Distinct Words");
		//
		List<Word> wordsRootExtraction = new ArrayList<Word>();
		for(String wordStr: words) {
			Word word = WordsUnreferencedDB.selectWordByWordValue(wordStr);
			
			if( word != null && !word.getWord().equalsIgnoreCase(word.getRootWord())) {
				word.setRootWord(word.getWord());
				word.setIsRootWordExtracted("Y");
				wordsRootExtraction.add(word);
			}
			
		}
		
		System.out.println(wordsRootExtraction.size() + " words will be marked as root root");
		
		
		WordsUnreferencedDB.updateWordTagRootWords(wordsRootExtraction);
	}
	
	/**
	 * This method pulls all the root words from dataase and then appends the compound word endings, then queries the databse to see if that word exists.
	 * If that compound word exits, then it marks it's root word.
	 * 
	 * The idea is if you already know नेपाल is a root word, then the  नेपालमा, नेपालभित्र, नेपालसम्म etc should also have नेपाल as root word.
	 * 
	 */
	   public static void updateCompoundWordRootsFromKnownRoots() {
	        //find all the distinct root words
	        List<String> words = WordsUnreferencedDB.getDistinctRootWords();
	        System.out.println("Found: " + words.size() + "Distinct Words");
	        //
	        List<Word> wordsRootExtraction = new ArrayList<Word>();
	        for(String wordStr: words) {
	            System.out.println("Processing: " + wordStr);
	            
	            int foundCount = 0;
	            for(CompoundWordEnding cwe: CompoundWordEnding.values()) {
	            
	                
    	            Word word = WordsUnreferencedDB.selectWordByWordValue(wordStr+cwe.getNepaliWordEnding());
    	            
    	            if( word != null && !word.getWord().equalsIgnoreCase(word.getRootWord())) {
    	                word.setRootWord(word.getWord());
    	                word.setIsRootWordExtracted("Y");
    	                wordsRootExtraction.add(word);
    	                
    	                foundCount++;
    	            }
	            }
	            
	            System.out.println("Found " + foundCount++ + " Similar Words for " + wordStr);
	            
	        }
	        
	        System.out.println(wordsRootExtraction.size() + " words will be marked to have roots");
	        
	        
	        WordsUnreferencedDB.updateWordTagRootWords(wordsRootExtraction);
	    }
	   
	   
	   public static void updateRomanizationISO(String rootWord, String valueRomanizedISOStandard) {
	       
	       System.out.println(rootWord + " " + valueRomanizedISOStandard);
	       
	       WordsUnreferencedDB.updateRomanizationISO(rootWord, valueRomanizedISOStandard);
	   }
	   
	   /**
	    * Returns a list of unverified probable names based on @see {@link NameWordEnding}
	    * @param limit
	    * @return
	    */
	   public static List<Word> selectUnverifiedNames(int limit) {
	       return WordsUnreferencedDB.selectUnverifiedNames(limit);
	   }
	   
       /**
        * Returns a list of unverified probable names based on @see {@link LocationWorddEnding}
        * @param limit
        * @return
        */
       public static List<Word> selectUnverifiedLocations(int limit) {
           return WordsUnreferencedDB.selectUnverifiedLocations(limit);
       }
    
	
}

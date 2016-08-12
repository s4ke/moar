package com.github.s4ke.moar.lucene.query.test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.s4ke.moar.MoaPattern;
import com.github.s4ke.moar.lucene.query.MoarQuery;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Martin Braun
 */
public class MoarQueryTest extends BaseLuceneTest {

	private static final Version VERSION = Version.LUCENE_6_1_0;

	private static final String UNIQUE = "unique";

	private static final List<String> WORDS = Arrays.asList(
			"toast",
			"marmalade",
			"peanutbutter",
			"jelly",
			"moar",
			"lucene",
			"regex",
			"hello",
			"bye",
			"bread",
			"baguette",
			"pizza",
			"kebap",
			"chili",
			"pepperoni",
			"space"
	);

	private static Random RANDOM;
	private static final int WORD_COUNT_PER_DOCUMENT = 50;

	private static String randomString(int words) {
		StringBuilder ret = new StringBuilder();
		for ( int i = 0; i < words; ++i ) {
			ret.append( WORDS.get( RANDOM.nextInt( WORDS.size() ) ) ).append( " " );
		}
		return ret.toString();
	}

	private Directory d;

	@Before
	public void setup() throws IOException {
		RANDOM = new Random( 16812875 );
		this.d = FSDirectory.open( Paths.get( "lucene_dir", "moarquery" ) );
		this.setupData();
	}

	private void setupData() throws IOException {
		System.out.println( "clearing index" );
		{
			WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();

			IndexWriterConfig iwc = new IndexWriterConfig( analyzer );
			try (IndexWriter iw = new IndexWriter( this.d, iwc )) {
				iw.deleteAll();
			}
		}

		System.out.println( "writing into index" );
		WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();

		IndexWriterConfig iwc = new IndexWriterConfig( analyzer );
		try (IndexWriter iw = new IndexWriter( this.d, iwc )) {


			{
				Document doc = createDocument();
				Field idField = new Field( "id", String.valueOf( -1 ), ID_FIELD_TYPE );
				Field field = new Field( "tag", UNIQUE, TAGS_FIELD_TYPE );
				doc.add( field );
				doc.add( idField );
				iw.addDocument( doc );
			}

			for ( int i = 0; i < 100; ++i ) {
				Document doc = createDocument();
				Field idField = new Field( "id", String.valueOf( i ), ID_FIELD_TYPE );
				Field field = new Field( "tag", randomString( WORD_COUNT_PER_DOCUMENT ), TAGS_FIELD_TYPE );
				doc.add( field );
				doc.add( idField );
				iw.addDocument( doc );
				if ( i % 10 == 0 ) {
					System.out.println( i );
				}

			}
			iw.commit();
		}
		System.out.println( "finished setting up index data" );
	}

	@Test
	public void testBasics() throws IOException {
		try (IndexReader ir = DirectoryReader.open( d )) {
			IndexSearcher is = new IndexSearcher( ir );

			MoaPattern pattern = MoaPattern.compile( UNIQUE );
			MoarQuery tq = new MoarQuery( "tag", pattern );

			TopDocs td = is.search( tq, 10 );
			assertEquals( 1, td.totalHits );
		}
	}

	@After
	public void tearDown() throws IOException {
		this.d.close();
	}
}

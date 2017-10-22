package com.jt.jt_lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class TestDemo {
	
	@Test
	public void createIndex() throws IOException{
		
		FSDirectory directory=FSDirectory.open(new File("./index"));
		
		Analyzer analyzer=new StandardAnalyzer();
		
		IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_4_10_2,analyzer);
		
		IndexWriter writer=new IndexWriter(directory,config);
		
		Document doc=new Document();
		
		doc.add(new TextField("field1","hello you world",Store.YES));
		
		doc.add(new TextField("field2","happy  may new year world you ",Store.YES));
		
		writer.addDocument(doc);
		
		writer.commit();
		
		writer.close();
		
		directory.close();
		
		
		
	}
	
	
	@Test
	public void search() throws IOException{
		FSDirectory directory=FSDirectory.open(new File("./index"));
		
		
		IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(directory));
		
		TermQuery query=new TermQuery(new Term("field1","world"));
		
		
		TopDocs topDocs = searcher.search(query, 5);
		
		
		System.out.println(topDocs.scoreDocs.length);
		
		for (ScoreDoc doc : topDocs.scoreDocs) {
			Document document = searcher.doc(doc.doc);
			System.out.println("得分:"+doc.score+"内容:"+document.get("field1"));
		}
		
		
	}

}

package com.ltt.Index;

import com.ltt.Utils.GlobalVariances;
import com.ltt.Utils.FileModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class IndexFactory
{
    private IndexWriter indexWriter = null;
    private Integer commit_cnt = 0;
    private Integer commit_limit = 0;

    public FieldType Gen_New_FieldType(List<Integer> paras)
    {
        Integer stored = paras.get(0);
        Integer tokenized = paras.get(1);
        Integer indexopt = paras.get(2);
        FieldType ft = new FieldType();
        ft.setStored(GlobalVariances.booleanList.get(stored));
        ft.setTokenized(GlobalVariances.booleanList.get(tokenized));
        ft.setIndexOptions(GlobalVariances.indexOptionsList.get(indexopt));
        if (stored > 0 && indexopt < 4)
        {
            ft.setStoreTermVectors(true);
            ft.setStoreTermVectorOffsets(true);
            ft.setStoreTermVectorPayloads(true);
            ft.setStoreTermVectorPositions(true);
        }
        return ft;
    }

    private void checkCommit() { this.checkCommit(false); }
    private void checkCommit(Boolean force)
    {
        commit_cnt += 1;
        if (commit_cnt >= commit_limit || force)
        {
            try
            {
                indexWriter.commit();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            commit_cnt = 0;
        }
    }

    public void closeWriter()
    {
        try
        {
            indexWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addIndex(Document doc, String fieldName, String content, List<Integer> fieldPara)
    {
        doc.add(new Field(fieldName, content, Gen_New_FieldType(fieldPara)));
    }

    public void addIndex(Document doc, String fieldName, String content, FieldType ft)
    {
        doc.add(new Field(fieldName, content, ft));
    }

    public void commitDocument(Document doc)
    {
        try
        {
            indexWriter.addDocument(doc);
            checkCommit();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Change index writer configurations to make index faster
    // MaxBufferedDocs : (default 10), larger -> faster
    // MergeFactor : (default 10), larger -> faster

    public void Initialize(String storeDir, Integer commit_lim, Analyzer analyzer)
    {
        commit_limit = commit_lim;
        FileModel.CreateFolder(storeDir);
        try
        {
            Directory dir = MMapDirectory.open(Paths.get(storeDir));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setMaxBufferedDocs(100);
            indexWriter = new IndexWriter(dir, config);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}

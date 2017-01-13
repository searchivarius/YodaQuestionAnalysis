package edu.cmu.lti.oaqa.test;

import java.io.*;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import cz.brmlab.yodaqa.model.Question.QuestionInfo;

public class TextCollectionReader extends CasCollectionReader_ImplBase {


  public static final String INPUT_FILE = "inputFile";
  @ConfigurationParameter(name = INPUT_FILE, mandatory = true)
  private String mInputFile;
  
  BufferedReader mInput;
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    mLineIndex = -1;
    try {
      mInput = new BufferedReader(new FileReader(mInputFile));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  protected void readNextLine() {
    mLineIndex++;
    try {
      mCurrLine = mInput.readLine();
    } catch (IOException io) {
      io.printStackTrace();
      mCurrLine = null;
    }
  }

  @Override
  public boolean hasNext() throws CollectionException {
    if (mCurrLine == null)
      readNextLine();
    return mCurrLine != null;
  }  

  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    if (mCurrLine == null)
      readNextLine();
    aCAS.setDocumentLanguage("en");
    aCAS.setDocumentText(mCurrLine);
    try {
      QuestionInfo q = new QuestionInfo(aCAS.getJCas());
      q.addToIndexes();
    } catch (CASException e) {
      e.printStackTrace();
      throw new CollectionException(e);
    }
    mCurrLine = null;
  }

  @Override
  public void close() throws IOException {
    mInput.close();
  }

  @Override
  public Progress[] getProgress() {
    return new Progress[]{new ProgressImpl(mLineIndex, -1, Progress.ENTITIES)};  }


  int     mLineIndex;
  String  mCurrLine;
}

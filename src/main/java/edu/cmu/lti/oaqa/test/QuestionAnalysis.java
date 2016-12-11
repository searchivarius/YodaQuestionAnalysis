package edu.cmu.lti.oaqa.test;

import org.apache.uima.UimaContext;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class QuestionAnalysis extends JCasAnnotator_ImplBase {
//  private static final Logger logger = LoggerFactory.getLogger(QuestionAnalysis.class);

  private AnalysisEngine mEngine;
  
  public static final String DUMPCAS_FILE = "dumpcasFile";
  
  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException{
    mEngine = createEngine(QuestionAnalysisAE.createEngineDescription((String)context.getConfigParameterValue(DUMPCAS_FILE)));
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    mEngine.process(aJCas);
  }

}

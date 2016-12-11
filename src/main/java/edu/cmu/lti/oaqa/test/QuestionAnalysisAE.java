package edu.cmu.lti.oaqa.test;

/* 
 * Based on the question analysis module from YodaQA
 * The biggest difference is that we don't extract DBPedia concepts here:
 * CluesToConcepts is commented out!
 */ 

import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.CasDumpWriter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.brmlab.yodaqa.analysis.question.*;

import cz.brmlab.yodaqa.analysis.tycor.LATByWordnet;
import cz.brmlab.yodaqa.io.debug.DumpConstituents;
import cz.brmlab.yodaqa.provider.OpenNlpNamedEntities;

/**
 * Annotate the QuestionCAS.
 *
 * This is a descriptor for an aggregate AE that will run a variety of annotators on the QuestionCAS. 
 */

public class QuestionAnalysisAE {
	final static Logger logger = LoggerFactory.getLogger(QuestionAnalysisAE.class);

	public static AnalysisEngineDescription createEngineDescription(String dumpCasFile) throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();

		/* Token features: */

		builder.add(AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class));

		/* POS, constituents, dependencies: */
		// fast, reliable
		builder.add(AnalysisEngineFactory.createEngineDescription(StanfordParser.class,
					StanfordParser.PARAM_WRITE_POS, true));

		/* Lemma features: */

		// fastest and handling numbers correctly:
		builder.add(AnalysisEngineFactory.createEngineDescription(LanguageToolLemmatizer.class));

		/* Named Entities: */
		builder.add(OpenNlpNamedEntities.createEngineDescription());

		/* Okay! Now, we can proceed with our key tasks. */

		builder.add(AnalysisEngineFactory.createEngineDescription(FocusGenerator.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(FocusNameProxy.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(SubjectGenerator.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(SVGenerator.class));

		/* Prepare LATs */
		builder.add(AnalysisEngineFactory.createEngineDescription(LATByFocus.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(LATBySV.class));
		/* Generalize imprecise LATs */
		builder.add(AnalysisEngineFactory.createEngineDescription(LATByWordnet.class,
					LATByWordnet.PARAM_EXPAND_SYNSET_LATS, false));

		/* Generate clues; the order is less specific to more specific */
		builder.add(AnalysisEngineFactory.createEngineDescription(ClueByTokenConstituent.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(ClueBySV.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(ClueByNE.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(ClueByLAT.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(ClueBySubject.class));
		/* Convert some syntactic clues to concept clues */
		//builder.add(AnalysisEngineFactory.createEngineDescription(CluesToConcepts.class));
		/* Merge any duplicate clues */
		builder.add(AnalysisEngineFactory.createEngineDescription(CluesMergeByText.class));


    // We won't use the dashboard
		//builder.add(AnalysisEngineFactory.createEngineDescription(DashboardHook.class));

		/* Classify question into classes*/
		builder.add(AnalysisEngineFactory.createEngineDescription(ClassClassifier.class));
		/* Some debug dumps of the intermediate CAS. */
		if (dumpCasFile != null && !dumpCasFile.trim().isEmpty()) {
			builder.add(AnalysisEngineFactory.createEngineDescription(DumpConstituents.class));
			builder.add(AnalysisEngineFactory.createEngineDescription(
				CasDumpWriter.class,
				CasDumpWriter.PARAM_OUTPUT_FILE, dumpCasFile));
		}

		AnalysisEngineDescription aed = builder.createAggregateDescription();
		aed.getAnalysisEngineMetaData().setName("edu.cmu.lti.oaqa.test.QuestionAnalysisAE");
		return aed;
	}
}

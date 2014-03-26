package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

public abstract class MeasureMechanismSpecIterator implements Iterator<Double> {

	public MeasureMechanismSpecIterator init(JCas spec, String viewName, AnnotationFS covering, Class<? extends Annotation> type, Annotation textAnno) throws MeasureMechanismException {
		try {
			if (covering != null) {
				specIterator = JCasUtil.iterator(covering, type, true, true);
			}
			else {
				JCas view = spec.getView(viewName);
				specIterator = JCasUtil.iterator(view, type);
			}
			this.textAnno = textAnno;
	
			// A little Java trick, to allow doing several things in a one-liner:
			// instantiate this object, call init(), and use it inside an Aggregator method
			return this;
		} catch (CASException e) {
			throw new MeasureMechanismException(e);
		}
	}
	
	@Override
	public Double next() {
		try {
			Annotation specElement = specIterator.next();
			return calcScore(textAnno, specElement);
		} catch (MeasureMechanismException e) {
			throw new MeasureMechanismRuntimeException(e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return specIterator.hasNext();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException(String.format("%s does not support removing spec items", this.getClass().getSimpleName()));
	}
	
	public abstract Double calcScore(Annotation text, Annotation spec) throws MeasureMechanismException;
	
	protected Iterator<? extends Annotation> specIterator;
	protected Annotation textAnno;
}

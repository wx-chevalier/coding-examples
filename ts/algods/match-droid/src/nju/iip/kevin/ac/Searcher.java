package nju.iip.kevin.ac;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator returns a list of Search matches.
 */
class Searcher implements Iterator {
	private SearchResult currentResult;
	private AhoCorasick tree;

	public Searcher(AhoCorasick tree, SearchResult result) {
		this.tree = tree;
		this.currentResult = result;
	}

	public boolean hasNext() {
		return (this.currentResult != null);
	}

	public Object next() {
		if (!hasNext())
			throw new NoSuchElementException();
		Object result = currentResult;
		currentResult = tree.continueSearch(currentResult);
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}

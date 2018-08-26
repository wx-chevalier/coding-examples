package nju.iip.kevin.ac;

import java.util.Set;

/**
 * <p>
 * Holds the result of the search so far. Includes the outputs where the search
 * finished as well as the last index of the matching.
 * </p>
 * 
 * <p>
 * (Internally, it also holds enough state to continue a running search, though
 * this is not exposed for public use.)
 * </p>
 */
public class SearchResult {
	State lastMatchedState;
	byte[] bytes;
	int lastIndex;

	SearchResult(State s, byte[] bs, int i) {
		this.lastMatchedState = s;
		this.bytes = bs;
		this.lastIndex = i;
	}

	/**
	 * Returns a list of the outputs of this match.
	 */
	public Set getOutputs() {
		return lastMatchedState.getOutputs();
	}

	/**
	 * Returns the index where the search terminates. Note that this is one byte
	 * after the last matching character.
	 */
	public int getLastIndex() {
		return lastIndex;
	}
}

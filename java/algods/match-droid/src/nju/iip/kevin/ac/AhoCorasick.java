package nju.iip.kevin.ac;

import java.util.Iterator;

/**
 * AhoCorasick(AC)算法的主要实现类。
 * */
public class AhoCorasick {
	
	
	/**
	 * 变量定义区
	 */
	/*AC搜索树的根节点*/
	private State root;
	/*prepared变量指向搜索树是否建立完毕*/
	private boolean prepared;

	/**
	 * 变量定义完毕
	 */
	
	/*无参构造函数*/
	public AhoCorasick() {
		
		/*建立一个深度为0的树结点*/
		this.root = new State(0);
		/*设置搜索树尚未建立*/
		this.prepared = false;
	}

	/**
	 * @author kevin_bear(王下邀月熊)
	 * @function 将输入的keyword数组加到搜索树中，output返回结果即使在搜索树中
	 * 			 SearchResults.getOutputs()返回的失败结点
	 */
	public void add(byte[] keyword, Object output) {
		if (this.prepared){
			throw new IllegalStateException("can't add keywords after prepare() is called.");
		}
		State lastState = this.root.extendAll(keyword);
		lastState.addOutput(output);
	}

	/**
	 * Prepares the automaton for searching. This must be called before any
	 * searching().
	 */
	public void prepare() {
		this.prepareFailTransitions();
		this.prepared = true;
	}

	/**
	 * Starts a new search, and returns an Iterator of SearchResults.
	 */
	public Iterator search(byte[] bytes) {
		return new Searcher(this, this.startSearch(bytes));
	}

	/**
	 * DANGER DANGER: dense algorithm code ahead. Very order dependent.
	 * Initializes the fail transitions of all states except for the root.
	 */
	private void prepareFailTransitions() {
		Queue q = new Queue();
		for (int i = 0; i < 256; i++) {
			if (this.root.get((byte) i) != null) {
				this.root.get((byte) i).setFail(this.root);
				q.add(this.root.get((byte) i));
			}else{
				this.root.put((byte) i, this.root);
			}
		}
		
		while (!q.isEmpty()) {
			State state = q.pop();
			byte[] keys = state.keys();			
			for (int i = 0; i < keys.length; i++) {				
				State r = state;
				byte a = keys[i];
				State s = r.get(a);
				q.add(s);
				r = r.getFail();
				while (r.get(a) == null)
					r = r.getFail();
				s.setFail(r.get(a));
				s.getOutputs().addAll(r.get(a).getOutputs());
			}
		}
	}

	/**
	 * Returns the root of the tree. Package protected, since the user probably
	 * shouldn't touch this.
	 */
	State getRoot() {
		return this.root;
	}

	/**
	 * Begins a new search using the raw interface. Package protected.
	 */
	SearchResult startSearch(byte[] bytes) {
		if (!this.prepared){
			throw new IllegalStateException("can't start search until prepare()");
		}
		return continueSearch(new SearchResult(this.root, bytes, 0));
	}

	/**
	 * Continues the search, given the initial state described by the
	 * lastResult. Package protected.
	 */
	SearchResult continueSearch(SearchResult lastResult) {
		byte[] bytes = lastResult.bytes;
		State state = lastResult.lastMatchedState;
		for (int i = lastResult.lastIndex; i < bytes.length; i++) {
			byte b = bytes[i];					
			while (state.get(b) == null){
				state = state.getFail();
			}
			state = state.get(b);			
			if (state.getOutputs().size() > 0){	
				return new SearchResult(state, bytes, i + 1);
			}
			
		}
		return null;
	}

	public static void main(String args[])
	{
	       AhoCorasick tree = new AhoCorasick();
	       tree.add("张梓雄".getBytes(), "张梓雄");
	       tree.add("张二狗".getBytes(), "张二狗");
	       tree.prepare();

	       Iterator searcher = tree.search("天天爱消除苏东航飞洒发哪款二狗张二狗".getBytes());
	       while (searcher.hasNext()) {
	           SearchResult result = (SearchResult) searcher.next();
	           System.out.println(result.getOutputs());
	           System.out.println("Found at index: " + result.getLastIndex());
	       }
	}
}

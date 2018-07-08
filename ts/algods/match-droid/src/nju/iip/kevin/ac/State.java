package nju.iip.kevin.ac;

import java.util.HashSet;
import java.util.Set;

/**
 * State类代表AC树中每一个结点的状态信息
 */

class State {

	/*该常量作为一个阈值来决定是否使用稀疏矩阵*/
	/*如果树的深度超过了既定阈值（目前选定的是3）则自动使用稀疏矩阵来表示边*/

	private static final int THRESHOLD_TO_USE_SPARSE = 3;

	private int depth;
	private EdgeList edgeList;
	private State fail;
	
	/*outputs表示已当前结点为失败结点的所有可能返回值*/
	private Set outputs;
	
	/**
	 * @function 含参构造函数
	 * @param depth：初始树的深度
	 */
	public State(int depth) {
		this.depth = depth;
		if (depth > THRESHOLD_TO_USE_SPARSE) {
			this.edgeList = new SparseEdgeList();
		} else {
			this.edgeList = new DenseEdgeList();
		}
		this.fail = null;
		this.outputs = new HashSet();
	}

	public State extend(byte b) {
		if (this.edgeList.get(b) != null){
			return this.edgeList.get(b);
		}
		State nextState = new State(this.depth + 1);
		this.edgeList.put(b, nextState);
		return nextState;
	}
	
	/**
	 * 
	 * @param bytes:输入的关键字字节数组
	 * @return
	 */
	public State extendAll(byte[] bytes) {
		/*state指向当前结点*/
		State state = this;
		for (int i = 0; i < bytes.length; i++) {
			/*判断当前的关键字节是否已经是当前结点的一条边*/
			if (state.edgeList.get(bytes[i]) != null){
				state = state.edgeList.get(bytes[i]);
			}else{
				state = state.extend(bytes[i]);
			}
		}

		return state;
	}
	/**
	 * Returns the size of the tree rooted at this State. Note: do not call this
	 * if there are loops in the edgelist graph, such as those introduced by
	 * AhoCorasick.prepare().
	 */
	public int size() {
		byte[] keys = edgeList.keys();
		int result = 1;
		for (int i = 0; i < keys.length; i++){
			result += edgeList.get(keys[i]).size();
		}
		return result;
	}

	public State get(byte b) {
		return this.edgeList.get(b);
	}

	public void put(byte b, State s) {
		this.edgeList.put(b, s);
	}

	public byte[] keys() {
		return this.edgeList.keys();
	}

	public State getFail() {
		return this.fail;
	}

	public void setFail(State f) {
		this.fail = f;
	}

	public void addOutput(Object o) {
		this.outputs.add(o);
	}

	public Set getOutputs() {
		return this.outputs;
	}
}

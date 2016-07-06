package com.github.s4ke.moar.moa.states;

import java.util.Map;

import com.github.s4ke.moar.strings.EfficientString;

/**
 * @author Martin Braun
 */
public class BasicState implements State {

	public int idx;
	public final EfficientString string;

	public BasicState(int idx, String string) {
		this.idx = idx;
		this.string = new EfficientString( string );
	}

	@Override
	public int getIdx() {
		return this.idx;
	}

	@Override
	public EfficientString getEdgeString(Map<String, Variable> variables) {
		return this.string;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public void touch() {

	}

	@Override
	public String toString() {
		return "BasicState{" +
				"idx=" + idx +
				", string='" + string + '\'' +
				'}';
	}
}
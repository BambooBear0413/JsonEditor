package io.bamboobear.json_editor.editor;

import java.util.ArrayList;
import java.util.List;

public abstract class Change {
	private final List<Change> siblings = new ArrayList<>();

	Change previous;
	Change next;

	private boolean isSibling;

	final void undoChanges() {
		this.undo();
		siblings.forEach(Change::undo);
	}

	final void redoChanges() {
		this.redo();
		siblings.forEach(Change::redo);
	}

	protected abstract void undo();
	protected abstract void redo();

	public final void append(Change change) {
		if(change.isSibling) throw new IllegalArgumentException("The change has been appended to another change.");

		boolean isChangeIndependent = (change.previous == null && change.next == null);

		if(!isChangeIndependent && change.previous != this) {
			throw new IllegalArgumentException("The change to be appended should be independent or immediately following this change.");
		}

		if(next != null && next != change) {
			throw new IllegalArgumentException("The change cannot be appended to this change since this change is followed by another change.");
		}

		if(change.previous == this) {
			this.next = change.next;
			if(change.next != null) change.next.previous = this;

			change.previous = change.next = null;
		}

		siblings.add(change);
		siblings.addAll(change.siblings);
		change.siblings.clear();
		change.isSibling = true;
	}
}

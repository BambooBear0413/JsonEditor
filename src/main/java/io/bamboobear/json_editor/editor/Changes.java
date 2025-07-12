package io.bamboobear.json_editor.editor;

import io.bamboobear.json_editor.component.json.JsonBooleanComponent;
import io.bamboobear.json_editor.component.json.JsonComponent;
import io.bamboobear.json_editor.component.json.JsonCompositeComponent;
import io.bamboobear.json_editor.component.json.JsonNumberComponent;
import io.bamboobear.json_editor.component.json.JsonPrimitiveComponent;
import io.bamboobear.json_editor.component.json.JsonStringComponent;

public final class Changes {
	private Changes() {}

	private static void doAdd(JsonComponent<?> json, JsonCompositeComponent<?> parent, int index) {
		parent.addElement(json, index);
		json.requestFocus();
		parent.refresh();
	}

	private static void doRemove(JsonComponent<?> json, JsonCompositeComponent<?> parent) {
		parent.removeElement(json);
		parent.refresh();
	}

	public static final class KeyFieldChange extends Change {
		private final JsonComponent<?> json;
		private final String before;
		private final String after;

		public KeyFieldChange(JsonComponent<?> json, String before, String after) {
			this.json = json;
			this.before = before;
			this.after = after;
		}

		@Override protected void undo() { json.setKey(before); }

		@Override protected void redo() { json.setKey(after); }
	}

	public static final class ValueFieldChange extends Change {
		private final JsonPrimitiveComponent<?> json;
		private final String before;
		private final String after;

		public ValueFieldChange(JsonPrimitiveComponent<?> json, String before, String after) {
			this.json = json;
			this.before = before;
			this.after = after;
		}

		@Override protected void undo() { doValueChange(json, before); }

		@Override protected void redo() { doValueChange(json, after); }

		private static void doValueChange(JsonPrimitiveComponent<?> json, String value) {
			switch(json) {
				case JsonBooleanComponent jbc -> jbc.setValue(Boolean.valueOf(value));
				case JsonNumberComponent jnc -> jnc.setValueFromString(value);
				case JsonStringComponent jsc -> jsc.setValue(value);
			}
		}
	}

	public static final class AddElementChange extends Change {
		private final JsonComponent<?> json;
		private final JsonCompositeComponent<?> parent;
		private final int index;

		public AddElementChange(JsonComponent<?> json,  JsonCompositeComponent<?> parent, int index) {
			this.json = json;
			this.parent = parent;
			this.index = index;
		}

		@Override protected void undo() { Changes.doRemove(json, parent); }

		@Override protected void redo() { Changes.doAdd(json, parent, index); }
	}

	public static final class RemoveElementChange extends Change {
		private final JsonComponent<?> json;
		private final JsonCompositeComponent<?> parent;
		private final int index;

		public RemoveElementChange(JsonComponent<?> json,  JsonCompositeComponent<?> parent, int index) {
			this.json = json;
			this.parent = parent;
			this.index = index;
		}

		@Override protected void undo() { Changes.doAdd(json, parent, index); }

		@Override protected void redo() { Changes.doRemove(json, parent); }
	}

	// TODO ReplaceElementChange

	// TODO ElementPositionChange
}

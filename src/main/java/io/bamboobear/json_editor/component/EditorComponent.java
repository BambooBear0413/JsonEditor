package io.bamboobear.json_editor.component;

public sealed interface EditorComponent permits EditorTextField, EditorComboBox{
	public default String translateInputValue(String input) {
		if(input == null) return "";

		StringBuffer sb = new StringBuffer(input.length());
		char[] chars = input.toCharArray();
		
		char c;
		for(int i = 0; i < chars.length; i++) {
			c = chars[i];
			switch(c) {
			case '\b' -> sb.append("\\b");
			case '\f' -> sb.append("\\f");
			case '\n' -> sb.append("\\n");
			case '\r' -> sb.append("\\r");
			case '\t' -> sb.append("\\t");
			case '\\' -> sb.append("\\\\");
			default -> sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	public default String translateOutputValue(String input) {
		if(input == null) return "";
		
		StringBuffer sb = new StringBuffer(input.length());
		char[] chars = input.toCharArray();

		char c;
		for(int i = 0; i < chars.length; i++) {
			c = chars[i];
			if(c != '\\') {
				sb.append(c);
				continue;
			}
			
			if(i + 1 == chars.length) {
				sb.append('\\');
				continue;
			}
			
			char c1 = chars[++i];
			switch(c1) {
			case '\\' -> sb.append('\\');
			case 'b' -> sb.append('\b');
			case 'f' -> sb.append('\f');
			case 'n' -> sb.append('\n');
			case 'r' -> sb.append('\r');
			case 't' -> sb.append('\t');
			case 'u' -> {
				if(i + 4 >= chars.length) {
					sb.append("\\u");
					break;
				}
				
				String str = new String(new char[] {chars[i + 1], chars[i + 2], chars[i + 3], chars[i + 4]});
				try {
					char result = Character.toChars(Integer.parseInt(str, 16))[0];
					sb.append(result);
					i += 4;
				} catch (NumberFormatException e) {
					sb.append("\\u");
				}
			}
			default -> {
				sb.append('\\');
				sb.append(c1);
			}
			}
		}
		
		return sb.toString();
	}
}

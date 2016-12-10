package game_creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class XMLNode {
	private String elementName = "";
	private Map<String, String> attributes = new LinkedHashMap<>();
	List<XMLNode> children = new ArrayList<XMLNode>();
	private StringBuilder textContent = new StringBuilder();
	
	public XMLNode(String elementName) {
		this.elementName = elementName;
	}
	
	public XMLNode setAttr(String attr, String value) {
		attributes.put(attr, value);
		return this;
	}
	
	public void add(XMLNode... children) {
		textContent = null;
		List<XMLNode> nodes = Arrays.asList(children);
		nodes.removeIf(node -> node == null);
		this.children.addAll(nodes);
	}
	
	public void setText(Object obj) {
		textContent = new StringBuilder(String.valueOf(obj));
		children.clear();
	}
	
	public void appendText(Object obj) {
		textContent.append(obj);
		children.clear();
	}
	
	public XMLNode getChild(int index) {
		return children.get(index);
	}
	
	@Override
	public String toString() {
		boolean empty = children.isEmpty() && textContent.length() == 0;
		StringJoiner openTag = new StringJoiner(" ", "<" + elementName + (attributes.isEmpty() ? "" : " "), empty ? " />" : ">");
		StringBuilder xmlContent = new StringBuilder();
		String closeTag = "</" + elementName + ">";
		attributes.entrySet().forEach(attr -> openTag.add(attr.getKey() + "=\"" + attr.getValue() + "\""));
		xmlContent.append(openTag);
		if(!empty) {
			if(textContent.length() == 0)
				children.forEach(xmlContent::append);
			else
				xmlContent.append(textContent);
			xmlContent.append(closeTag);
		}
		return xmlContent.toString();
	}
}
package info.iconmaster.typhon.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.util.SourceInfo;

/**
 * This represents an annotation: An entity that can be attached to most Typhon language entities, and can have parameters.
 * This is used so the Typhon compiler can alter the system based on the user's wishes in a modular way.
 * 
 * @author iconmaster
 *
 */
public class AnnotationDefinition extends TyphonModelEntity implements MemberAccess {
	/**
	 * The name of the annotation. Must be a valid Typhon identifier.
	 */
	private String name;
	
	/**
	 * A list of the parameters this annotation can have.
	 */
	private List<Parameter> params = new ArrayList<>();
	
	/**
	 * Construct a new annotation.
	 * 
	 * @param tni
	 * @param name The name of the annotation. Must be a valid Typhon identifier.
	 */
	public AnnotationDefinition(TyphonInput tni, String name) {
		super(tni);
		this.name = name;
	}
	
	/**
	 * Construct a new annotation.
	 * 
	 * @param tni
	 * @param source
	 * @param name The name of the annotation. Must be a valid Typhon identifier.
	 */
	public AnnotationDefinition(TyphonInput tni, SourceInfo source, String name) {
		super(tni, source);
		this.name = name;
	}
	
	/**
	 * Construct a library annotation.
	 * 
	 * @param tni
	 * @param source
	 * @param name
	 */
	public AnnotationDefinition(TyphonInput tni, String name, Parameter[] params) {
		this(tni, name);
		getParams().addAll(Arrays.asList(params));
		markAsLibrary();
	}
	
	/**
	 * @return The name of the annotation.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The parameters of this annotation.
	 */
	public List<Parameter> getParams() {
		return params;
	}
	
	/**
	 * The package this annotation belongs to.
	 */
	private Package parent;

	/**
	 * @return The package this annotation belongs to.
	 */
	public Package getParent() {
		return parent;
	}

	/**
	 * NOTE: Don't call this, call <tt>{@link Package}.addAnnotDef()</tt> instead.
	 * 
	 * @param parent The new package this annotation belongs to.
	 */
	public void setParent(Package parent) {
		this.parent = parent;
	}

	@Override
	public MemberAccess getMemberParent() {
		return getParent();
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder("@");
		
		String path = getPathString();
		if (!path.isEmpty()) {
			sb.append(path);
			sb.append('.');
		}
		sb.append(getName());
		
		if (!params.isEmpty()) {
			sb.append('(');
			
			for (Parameter param : getParams()) {
				sb.append(param.prettyPrint());
				sb.append(',');
			}
			sb.deleteCharAt(sb.length()-1);
			
			sb.append(')');
		}
		
		return sb.toString();
	}
}

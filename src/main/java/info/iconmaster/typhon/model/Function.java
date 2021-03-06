package info.iconmaster.typhon.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.antlr.TyphonParser.ExprContext;
import info.iconmaster.typhon.antlr.TyphonParser.StatContext;
import info.iconmaster.typhon.antlr.TyphonParser.TypeContext;
import info.iconmaster.typhon.compiler.CodeBlock;
import info.iconmaster.typhon.types.FunctionType;
import info.iconmaster.typhon.types.TemplateType;
import info.iconmaster.typhon.types.Type;
import info.iconmaster.typhon.types.TypeRef;
import info.iconmaster.typhon.util.SourceInfo;

/**
 * This represents a Typhon function.
 * It has parameters, templates, and zero or more return types.
 * 
 * @author iconmaster
 *
 */
public class Function extends TyphonModelEntity implements MemberAccess {
	/**
	 * The name of this function.
	 * Must be a valid Typhon identifier.
	 * May be null (if this function is anonymous, for example).
	 */
	private String name;
	
	/**
	 * The return types of this function.
	 */
	private List<TypeRef> retType = new ArrayList<>();
	
	/**
	 * The template parameters for this function.
	 */
	private List<TemplateType> template = new ArrayList<>();
	
	/**
	 * The parameters for this function.
	 */
	private List<Parameter> params = new ArrayList<>();
	
	/**
	 * The code that is executed when this function is called.
	 */
	private CodeBlock code;
	
	/**
	 * The ANTLR rule representing the return types.
	 */
	private List<TypeContext> rawRetType;
	
	
	/**
	 * The form of the supplied function, as it was declared.
	 * Only set if this function derives from source code.
	 */
	private Form form;
	
	/**
	 * The ANTLR rule representing the function's code, if the function is of BLOCK form.
	 * Check the form to see what this list contains.
	 */
	private List<?> rawCode;
	
	/**
	 * This represents how the function was declared.
	 * 
	 * @author iconmaster
	 */
	public static enum Form {
		/**
		 * Block ('{}') form.
		 * rawCode should be a {@link List}<{@link StatContext}>.
		 */
		BLOCK,
		/**
		 * Expression ('=>') form.
		 * rawCode should be a {@link List}<{@link ExprContext}>.
		 */
		EXPR,
		/**
		 * Stub (';') form.
		 * rawCode should be null.
		 */
		STUB,
	}
	
	public Function(TyphonInput input, String name) {
		super(input);
		this.name = name;
	}
	
	public Function(TyphonInput input, SourceInfo source, String name) {
		super(input, source);
		this.name = name;
	}
	
	/**
	 * @return The name of this function. May be null (if this function is anonymous, for example).
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The return types of this function.
	 */
	public List<TypeRef> getRetType() {
		return retType;
	}

	/**
	 * @return The template parameters for this function.
	 */
	public List<TemplateType> getTemplate() {
		getTypePackage();
		return template;
	}

	/**
	 * @return The parameters for this function.
	 */
	public List<Parameter> getParams() {
		return params;
	}

	/**
	 * @return The code that is executed when this function is called.
	 */
	public CodeBlock getCode() {
		return code;
	}

	/**
	 * @param code The new code that is executed when this function is called.
	 */
	public void setCode(CodeBlock code) {
		this.code = code;
	}

	/**
	 * @return The ANTLR rule representing the return types.
	 */
	public List<TypeContext> getRawRetType() {
		return rawRetType;
	}

	/**
	 * @return The form of the function as it was declared.
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * @return The ANTLR rule representing the function's code. Check the form to see what this list contains.
	 */
	public List<?> getRawCode() {
		return rawCode;
	}
	
	/**
	 * Sets the raw ANTLR data for this function.
	 * 
	 * @param rawRetType The ANTLR rule representing the return types.
	 * @param form The form of the function as it was declared.
	 * @param rawCode The ANTLR rule representing the function's code. See {@link Form} for details.
	 */
	public void setRawData(List<TypeContext> rawRetType, Form form, List<?> rawCode) {
		super.setRawData();
		this.rawRetType = rawRetType;
		this.form = form;
		this.rawCode = rawCode;
	}
	
	/**
	 * The package this function belongs to.
	 */
	private Package parent;

	/**
	 * @return The package this function belongs to.
	 */
	public Package getParent() {
		return parent;
	}

	/**
	 * NOTE: Don't call this, call <tt>{@link Package}.addFunction()</tt> instead.
	 * 
	 * @param parent The new package this function belongs to.
	 */
	public void setParent(Package parent) {
		this.parent = parent;
	}
	
	@Override
	public List<TemplateType> getMemberTemplate() {
		return getTemplate();
	}
	
	/**
	 * Constructs a library function.
	 * 
	 * @param tni
	 * @param name
	 * @param args
	 * @param retTypes
	 */
	public Function(TyphonInput tni, String name, TemplateType[] template, Parameter[] args, TypeRef[] retTypes) {
		this(tni, name);
		
		getTemplate().addAll(Arrays.asList(template));
		getParams().addAll(Arrays.asList(args));
		getRetType().addAll(Arrays.asList(retTypes));
		
		markAsLibrary();
	}
	
	/**
	 * Constructs a library function.
	 * 
	 * @param tni
	 * @param name
	 * @param args
	 * @param retTypes
	 */
	public Function(TyphonInput tni, String name, TemplateType[] template, Parameter[] args, Type[] retTypes) {
		this(tni, name);
		
		getTemplate().addAll(Arrays.asList(template));
		getParams().addAll(Arrays.asList(args));
		getRetType().addAll(Arrays.asList(retTypes).stream().map((a)->new TypeRef(a)).collect(Collectors.toList()));
		
		markAsLibrary();
	}
	
	@Override
	public MemberAccess getMemberParent() {
		return getParent();
	}
	
	/**
	 * @return If this is an instance function: The type this function is part of. If this is a static function: Null.
	 */
	public Type getFieldOf() {
		if (hasAnnot(tni.corePackage.ANNOT_STATIC)) {
			return null;
		}
		
		MemberAccess access = this;
		while (access != null) {
			if (access instanceof Type) {
				return (Type) access;
			}
			access = access.getMemberParent();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Function("+getRetType()+" "+name+getParams()+")";
	}
	
	public Map<TemplateType, TypeRef> getFuncTemplateMap(Map<TemplateType, TypeRef> typeMap) {
		Map<TemplateType, TypeRef> result = new HashMap<>();
		result.putAll(typeMap);
		for (TemplateType t : getTemplate()) {
			result.put(t, t.getDefaultValue() == null ? t.getBaseType() : t.getDefaultValue());
		}
		return result;
	}
	
	private Package typePackage;
	
	public Package getTypePackage() {
		if (typePackage != null) {
			typePackage.getParent().removeSubpackage(typePackage);
		}
		
		typePackage = new Package(source, null, getParent() == null ? tni.corePackage : getParent()) {
			@Override
			public MemberAccess getMemberParent() {
				return Function.this;
			}
		};
		
		for (TemplateType t : template) {
			typePackage.addType(t);
		}
		
		return typePackage;
	}
	
	@Override
	public List<MemberAccess> getMembers(Map<TemplateType, TypeRef> templateMap) {
		return getTypePackage().getMembers(templateMap);
	}
	
	/**
	 * @return True if this field is static. False if it belongs to an instance of some type.
	 */
	public boolean isStatic() {
		return getFieldOf() == null;
	}
	
	private List<Function> virtualBases = new ArrayList<>();
	
	public List<Function> getVirtualBases() {
		return virtualBases;
	}
	
	private List<Function> virtualOverrides = new ArrayList<>();
	
	public List<Function> getVirtualOverrides() {
		if (isStatic()) {
			return null;
		}
		
		return virtualOverrides;
	}
	
	public static void setOverride(Function virtual, Function override) {
		virtual.getVirtualOverrides().add(override);
		override.getVirtualBases().add(virtual);
		
		if (!override.hasAnnot(override.tni.corePackage.ANNOT_OVERRIDE)) {
			override.getAnnots().add(new Annotation(override.tni.corePackage.ANNOT_OVERRIDE));
		}
	}
	
	public Function getVirtualBase(Type expected) {
		for (Function f : virtualBases) {
			if (expected.canCastTo(new TypeRef(expected), new TypeRef(f.getFieldOf()))) {
				return f;
			}
		}
		
		return this;
	}
	
	public Function getVirtualOverride(Type expected) {
		for (int i = virtualOverrides.size()-1; i >= 0; i--) {
			Function f = virtualOverrides.get(i);
			if (expected.canCastTo(new TypeRef(expected), new TypeRef(f.getFieldOf()))) {
				return f;
			}
		}
		
		return this;
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		
		String path = getPathString();
		if (!path.isEmpty()) {
			sb.append(path);
			sb.append('.');
		}
		sb.append(getName());
		
		sb.append('(');
		if (!params.isEmpty()) {
			for (Parameter param : getParams()) {
				sb.append(param.getType().prettyPrint());
				sb.append(',');
			}
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(')');
		
		return sb.toString();
	}
	
	public FunctionType asType() {
		FunctionType type = new FunctionType(tni, source);
		
		for (Parameter param : getParams()) {
			type.getArgTypes().add(param.getType());
		}
		
		for (TypeRef ret : getRetType()) {
			type.getRetTypes().add(ret);
		}
		
		return type;
	}
}

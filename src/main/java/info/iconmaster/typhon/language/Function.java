package info.iconmaster.typhon.language;

import java.util.ArrayList;
import java.util.List;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.antlr.TyphonParser.ExprContext;
import info.iconmaster.typhon.antlr.TyphonParser.StatContext;
import info.iconmaster.typhon.antlr.TyphonParser.TypeContext;
import info.iconmaster.typhon.tnil.CodeBlock;
import info.iconmaster.typhon.types.TemplateType;
import info.iconmaster.typhon.types.TypeRef;
import info.iconmaster.typhon.util.SourceInfo;

/**
 * This represents a Typhon function.
 * It has parameters, templates, and zero or more return types.
 * 
 * @author iconmaster
 *
 */
public class Function extends TyphonLanguageEntity {
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
	 */
	private List<StatContext> rawCodeBlockForm;
	
	/**
	 * The ANTLR rule representing the function's code, if the function is of EXPR form.
	 */
	private ExprContext rawCodeExprForm;
	
	/**
	 * This represents how the function was declared.
	 * 
	 * @author iconmaster
	 */
	public static enum Form {
		/**
		 * Block ('{}') form.
		 */
		BLOCK,
		/**
		 * Expression ('=>') form.
		 */
		EXPR,
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
	 * @return The ANTLR rule representing the function's code, if the function is of BLOCK form.
	 */
	public List<StatContext> getRawCodeBlockForm() {
		return rawCodeBlockForm;
	}

	/**
	 * @return The ANTLR rule representing the function's code, if the function is of EXPR form.
	 */
	public ExprContext getRawCodeExprForm() {
		return rawCodeExprForm;
	}
	
	/**
	 * Sets the raw ANTLR data for this function.
	 * 
	 * @param rawRetType The ANTLR rule representing the return types.
	 * @param form The form of the function as it was declared.
	 * @param rawCode The ANTLR rule representing the function's code. Either a {@link List}<{@link StatContext}> or an {@link ExprContext}.
	 * @throws IllegalArgumentException If rawCode is not a valid type, given the form supplied.
	 */
	public void setRawData(List<TypeContext> rawRetType, Form form, Object rawCode) {
		this.rawRetType = rawRetType;
		this.form = form;
		
		if (this.form == Form.BLOCK && rawCode instanceof List) {
			this.rawCodeBlockForm = ((List)rawCode);
		} else if (this.form == Form.EXPR && rawCode instanceof ExprContext) {
			this.rawCodeExprForm = ((ExprContext)rawCode);
		} else {
			throw new IllegalArgumentException("rawCode must be either List<StatContext> or ExprContext");
		}
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
}

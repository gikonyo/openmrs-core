package org.openmrs.arden;

import antlr.ASTPair;
import antlr.MismatchedTokenException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.openmrs.arden.parser.*;


/**
 * Implements the methods defined in the HQL base parser to keep the grammar
 * source file a little cleaner.  Extends the parser class generated by ANTLR.
 *
 * @author Joshua Davis (pgmjsd@sourceforge.net)
 */
public class ArdenParser extends ArdenBaseParser {
	/**
	 * A logger for this class.
	 */
//	private static final Log log = LogFactory.getLog( ArdenParser.class );

	private ParseErrorHandler parseErrorHandler;
//	private ASTPrinter printer = getASTPrinter();

//	private static ASTPrinter getASTPrinter() {
//		return new ASTPrinter( org.hibernate.hql.antlr.HqlTokenTypes.class );
//	}

	protected ArdenParser(TokenBuffer tokenBuf, int k) {
		super( tokenBuf, k );
		initialize();
	}

	public ArdenParser(TokenBuffer tokenBuf) {
		super( tokenBuf );
		initialize();
	}

	protected ArdenParser(TokenStream lexer, int k) {
		super( lexer, k );
		initialize();
	}

	public ArdenParser(TokenStream lexer) {
		super( lexer );
		initialize();
	}

	public ArdenParser(ParserSharedInputState state) {
		super( state );
		initialize();
	}
		/**
	 * @param input
	 */
	public ArdenParser(String input) {
		super( new ArdenLexer( new DataInputStream( new ByteArrayInputStream( input.getBytes() ) ) ) );
		initialize();
	}
	
//	public void startRule()throws RecognitionException, TokenStreamException {
//		super.startRule();
//	}
	
//	public void reportError(RecognitionException e) {
//		parseErrorHandler.reportError( e ); // Use the delegate.
//	}

//	public void reportError(String s) {
//		parseErrorHandler.reportError( s ); // Use the delegate.
//	}

//	public void reportWarning(String s) {
//		parseErrorHandler.reportWarning( s );
//	}

//	public ParseErrorHandler getParseErrorHandler() {
//		return parseErrorHandler;
//	}

	/**
	 * Overrides the base behavior to retry keywords as identifiers.
	 *
	 * @param token The token.
	 * @param ex    The recognition exception.
	 * @return AST - The new AST.
	 * @throws antlr.RecognitionException if the substitution was not possible.
	 * @throws antlr.TokenStreamException if the substitution was not possible.
	 */
	public AST handleIdentifierError(Token token, RecognitionException ex) throws RecognitionException, TokenStreamException {
//		if ( log.isDebugEnabled() ) {
//			log.debug( "handleIdentifierError() : " + token );
//		}
		
		System.err.println("handleIdentifierError...");
		// If the token can tell us if it could be an identifier...
		if ( token instanceof ArdenToken ) {
			
			ArdenToken ArdenToken = ( ArdenToken ) token;
			// ... and the token could be an identifer and the error is
			// a mismatched token error ...
			System.err.println(ArdenToken.toString());
			
			if ( ArdenToken.isPossibleID() && ( ex instanceof MismatchedTokenException ) ) {
				MismatchedTokenException mte = ( MismatchedTokenException ) ex;
				// ... and the expected token type was an identifier, then:
				System.err.println("Inside-1 - handleIdentifierError..." + mte.expecting
						);
				if ( mte.expecting == ArdenBaseParserTokenTypes.ID) {
					System.err.println("Inside-2 - handleIdentifierError...");
					// Use the token as an identifier.
					reportWarning( "using keyword  '"
							+ token.getText()
							+ "' as an identifier due to: " + mte.getMessage() );
					// Add the token to the AST.
					ASTPair currentAST = new ASTPair();
					token.setType( ArdenBaseParserTokenTypes.WEIRD_IDENT );
					astFactory.addASTChild( currentAST, astFactory.create( token ) );
					consume();
					AST identifierAST = currentAST.root;
					return identifierAST;
				}
			} // if
		} // if
		// Otherwise, handle the error normally.
		return super.handleIdentifierError( token, ex );
	}

	/**
	 * Returns an equivalent tree for (NOT (a relop b) ), for example:<pre>
	 * (NOT (GT a b) ) => (LE a b)
	 * </pre>
	 *
	 * @param x The sub tree to transform, the parent is assumed to be NOT.
	 * @return AST - The equivalent sub-tree.
	 */
/*	
	public AST negateNode(AST x) {
//		if ( log.isDebugEnabled() )
//			log.debug( printer.showAsString( x, "negateNode()") );
		switch ( x.getType() ) {
			case EQ:
				x.setType( NE );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (EQ a b) ) => (NE a b)
			case NE:
				x.setType( EQ );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (NE a b) ) => (EQ a b)
			case GT:
				x.setType( LE );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (GT a b) ) => (LE a b)
			case LT:
				x.setType( GE );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (LT a b) ) => (GE a b)
			case GE:
				x.setType( LT );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (GE a b) ) => (LT a b)
			case LE:
				x.setType( GT );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (LE a b) ) => (GT a b)
			case LIKE:
				x.setType( NOT_LIKE );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (LIKE a b) ) => (NOT_LIKE a b)
			case NOT_LIKE:
				x.setType( LIKE );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (NOT_LIKE a b) ) => (LIKE a b)
			case BETWEEN:
				x.setType( NOT_BETWEEN );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (BETWEEN a b) ) => (NOT_BETWEEN a b)
			case NOT_BETWEEN:
				x.setType( BETWEEN );
				x.setText( "{not}" + x.getText() );
				return x;	// (NOT (NOT_BETWEEN a b) ) => (BETWEEN a b)
			case NOT:
				return x.getFirstChild();			// (NOT (NOT x) ) => (x)
			default:
				return super.negateNode( x );		// Just add a 'not' parent.
		}
	}


	/**
	 * Post process equality expressions, clean up the subtree.
	 *
	 * @param x The equality expression.
	 * @return AST - The clean sub-tree.
	 */
/*
	public AST processEqualityExpression(AST x) {
		int type = x.getType();
		if ( type == EQ || type == NE ) {
			boolean negated = type == NE;
			if ( x.getNumberOfChildren() == 2 ) {
				AST a = x.getFirstChild();
				AST b = a.getNextSibling();
				// (EQ NULL b) => (IS_NULL b)
				if ( a.getType() == NULL && b.getType() != NULL ) {
					return createIsNullParent( b, negated );
				}
				// (EQ a NULL) => (IS_NULL a)
				else if ( b.getType() == NULL && a.getType() != NULL ) {
					return createIsNullParent( a, negated );
				}
				else {
					return x;
				}
			}
			else {
				return x;
			}
		}
		else {
			return x;
		}
	}

	private AST createIsNullParent(AST node, boolean negated) {
		node.setNextSibling( null );
		int type = negated ? IS_NOT_NULL : IS_NULL;
		String text = negated ? "is not null" : "is null";
		return ASTUtil.createParent( astFactory, type, text, node );
	}

	public void showAst(AST ast, PrintStream out) {
		showAst( ast, new PrintWriter( out ) );
	}

	private void showAst(AST ast, PrintWriter pw) {
		printer.showAst( ast, pw );
	}
*/
	private void initialize() {
		// Initialize the error handling delegate.
		System.err.println("Init...");
		parseErrorHandler = new ErrorCounter( this );
	}

}


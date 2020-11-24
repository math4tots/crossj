package crossj.hacks.cj;

public interface CJAstExpressionVisitor<R, A> {
    R visitMethodCall(CJAstMethodCallExpression e, A a);
    R visitName(CJAstNameExpression e, A a);
    R visitLiteral(CJAstLiteralExpression e, A a);
    R visitNew(CJAstNewExpression e, A a);
    R visitInferredGenericsMethodCall(CJAstInferredGenericsMethodCallExpression e, A a);
    R visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, A a);
    R visitLogicalNot(CJAstLogicalNotExpression e, A a);
    R visitEmptyMutableList(CJAstEmptyMutableListExpression e, A a);
    R visitNewUnion(CJAstNewUnionExpression e, A a);
    R visitLambda(CJAstLambdaExpression e, A a);
}

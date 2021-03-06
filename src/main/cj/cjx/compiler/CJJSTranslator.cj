package cjx.compiler

import cjx.compiler.CJJSSETranslator
import cjx.compiler.CJJSTypeTranslator
import cjx.compiler.CJLocalContext
import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJJSStringBuilder
import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJContext

class CJJSTranslator {
    static val _preludePath: String = "support/app/crossj.hacks.cj/prelude.js"
    val _ctx: CJContext
    val _out: CJJSStringBuilder

    def init(): Self {
        return Self.initWithContext(CJContext.init())
    }

    def initWithContext(ctx: CJContext): Self {
        val out = CJJSStringBuilder.init()
        return Self.new(ctx, out)
    }

    def _itemMetaName(itemName: String): String {
        return "MC$" + itemName.replace(".", "$")
    }

    def _itemMetaObjectName(itemName: String): String {
        return "MO$" + itemName.replace(".", "$")
    }

    def _ctor(itemName: String): String {
        return "CT$" + itemName.replace(".", "$")
    }

    def _field(fieldName: String): String {
        return "F$" + fieldName
    }

    def _method(methodName: String): String {
        return "M$" + methodName
    }

    def _typevar(typeVariableName: String): String {
        return "TV$" + typeVariableName
    }

    def _local(name: String): String {
        return "L$" + name
    }

    private def line(self: Self, s: String) {
        self._out.line(s)
    }

    private def lineStart(self: Self, s: String) {
        self._out.lineStart(s)
    }

    private def lineBody(self: Self, s: String) {
        self._out.lineBody(s)
    }

    private def lineEnd(self: Self, s: String) {
        self._out.lineEnd(s)
    }

    private def indent(self: Self) {
        self._out.indent()
    }

    private def dedent(self: Self) {
        self._out.dedent()
    }

    def translateWithMainClass(self: Self, maincls: String): Try[String] {
        self.line("(function(){")
        self.line("\"strict\";")
        self._emitPrelude()
        val itemNames = self._ctx.loadedItemNames().toList()
        for itemName in itemNames {
            self._emitItemPass1(itemName)?
        }
        for itemName in itemNames {
            self._emitItemPass2(itemName)?
        }
        self.line("})();")
        return Try.Ok(self._out.build())
    }

    def _emitPrelude(self: Self): Try[Unit] {
        self.lineBody(FS.read(Self._preludePath)?)
    }

    def _emitItemPass1(self: Self, fullName: String): Try[Unit] {
        # Translate constructors and methods
        val defn = self._ctx.loadItem(fullName)?
        if not defn.isNative() {
            self._emitNonNativeItemPass1(defn)?
        }
        return Try.Ok(())
    }

    def _emitNonNativeItemPass1(self: Self, defn: CJAstItemDefinition): Try[Unit] {
        val cjName = defn.fullName()
        val metaName = Self._itemMetaName(cjName)
        union defn.kind() {
            case Class {
                self._emitClassConstructor(defn)?
            }
        }
        self.line("class " + metaName + " {")
        self.indent()
        val typeParameters = defn.typeParameters()
        if typeParameters.toBool() {
            val tnames = typeParameters.map(tp -> Self._typevar(tp.name()))
            self.lineStart("constructor(")
            self.lineBody(tnames.get(0))
            for i = 1; i < tnames.size(); i += 1 {
                self.lineBody("," + tnames.get(i))
            }
            self.lineEnd(") {")
            self.indent()
            for tname in tnames {
                self.line("this." + tname + "=" + tname + ";")
            }
            self.dedent()
            self.line("}")
        }
        for member in defn.members() {
            union member {
                case Method(mdefn) {
                    self._emitNonNativeMethod(defn, mdefn)?
                }
            }
        }
        self.dedent()
        self.line("}")
        if typeParameters.isEmpty() {
            self.line(
                "const " +
                Self._itemMetaObjectName(cjName) +
                " = new " +
                metaName +
                "();")
        }
        return Try.Ok(())
    }

    def _emitClassConstructor(self: Self, defn: CJAstItemDefinition): Try[Unit] {
        val ctorName = Self._ctor(defn.fullName())
        val fields = defn.members().flatMap(mem -> union mem {
            case Field(field) = if (field.isInstance()) { [field] } else { [] }
            default = []
        })
        switch fields.size() {
            case 0 {
                self.line("function " + ctorName + "() { return null; }")
            }
            case 1 {
                self.line("function " + ctorName + "(x) { return x; }")
            }
            default {
                val fieldNames = fields.map(f -> Self._field(f.name()))
                self.line("function " + ctorName + "(")
                for i = 0; i < fieldNames.size(); i += 1 {
                    self.lineStart(if (i > 0) { "," } else { "" })
                    self.lineEnd(fieldNames.get(i))
                }
                self.line(") { return {")
                self.indent()
                for i = 0; i < fieldNames.size(); i += 1 {
                    val name = fieldNames.get(i)
                    self.lineStart(name + ":" + name)
                    self.lineEnd(if (i + 1 < fieldNames.size()) { "," } else { "" })
                }
                self.dedent()
                self.line("}}")
            }
        }
        return Try.Ok(())
    }

    def _emitNonNativeMethod(
            self: Self, itemDefn: CJAstItemDefinition, defn: CJAstMethodDefinition): Try[Unit] {
        val cjName = defn.name()
        val jsName = Self._method(cjName)
        val typeParameters = defn.typeParameters()
        val parameters = defn.parameters()
        val allJSParameterNames = (
            typeParameters.map(tp -> Self._typevar(tp.name())) +
            parameters.map(p -> Self._local(p.name())))
        self.lineStart(jsName + "(")
        for i = 0; i < allJSParameterNames.size(); i += 1 {
            if i > 0 {
                self.lineBody(",")
            }
            self.lineBody(allJSParameterNames.get(i))
        }
        self.lineEnd(") {")
        self.line("}")
        return Try.Ok(())
    }

    def _emitItemPass2(self: Self, fullName: String): Try[Unit] {
        # Inherit methods
        return Try.Ok(())
    }
}

package cjx.compiler.main

import cj.Process
import cjx.compiler.CJParserX

class RunParserX {
    def main() {
        val args = Process.argv()
        Self._main(args.get(0)).get()
    }

    def _main(root: String): Try[Unit] {
        for path in Self.rec(root)? {
            if path.endsWith(".cj") {
                IO.println(path + " ...")
                Self._dump(path)
            }
        }
        return Try.Ok(())
    }

    def rec(path: String): Try[List[String]] {
        val out = List[String].builder()
        val stack = @[path]
        while stack.toBool() {
            val p = stack.pop()
            val children = FS.list(p)?.sorted().map(base -> FS.join([p, base]))
            out.addAll(children.filter(c -> FS.isfile(c)))
            stack.addAll(children.filter(c -> FS.isdir(c)).reversed())
        }
        return Try.Ok(out.build())
    }

    def _dump(path: String): Try[Unit] {
        val data = FS.read(path)?
        val defn = CJParserX.parseString(path, data)?
        IO.println(defn.fullName())
        defn.comment().ifPresent(comment -> {
            IO.println(comment)
        })
        for member in defn.members() {
            member.case_().ifPresent(case_ -> {
                IO.println("  case " + case_.name() + "(" + ", ".join(case_.types()) + ")")
                case_.comment().ifPresent(comment -> {
                    Self._emitMemberComment(comment)
                })
            })
        }
        val staticMembers = defn.members().filter(m -> m.isStatic())
        val instanceMembers = defn.members().filter(m -> not m.isStatic())
        for member in staticMembers {
            member.field().ifPresent(field -> {
                IO.println("  static field " + field.name() + ": " + field.type())
                field.comment().ifPresent(comment -> {
                    Self._emitMemberComment(comment)
                })
            })
        }
        for member in staticMembers {
            member.method().ifPresent(method -> {
                val paramtypes = "(" + ", ".join(method.parameters().map(p -> p.type())) + ")"
                val rtype = method.returnType().map(t -> ": " + t.repr()).getOrDefault("")
                IO.println("  static method " + method.name() + paramtypes + rtype)
                method.comment().ifPresent(comment -> {
                    Self._emitMemberComment(comment)
                })
            })
        }
        for member in instanceMembers {
            member.field().ifPresent(field -> {
                IO.println("  field " + field.name() + ": " + field.type())
                field.comment().ifPresent(comment -> {
                    Self._emitMemberComment(comment)
                })
            })
        }
        for member in instanceMembers {
            member.method().ifPresent(method -> {
                val paramtypes = "(" + ", ".join(method.parameters().map(p -> p.type())) + ")"
                val rtype = method.returnType().map(t -> ": " + t.repr()).getOrDefault("")
                IO.println("  method " + method.name() + paramtypes + rtype)
                method.comment().ifPresent(comment -> {
                    Self._emitMemberComment(comment)
                })
            })
        }
        return Try.Ok(())
    }

    def _emitMemberComment(comment: String) {
        IO.println("    " + comment)
    }
}

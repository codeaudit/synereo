package com.biosimilarity.seleKt.model.ill.vm.illvm.Absyn; // Java Package generated by the BNF Converter.

public class UNUNIT extends Instruction {
  public final String illununit_;

  public UNUNIT(String p1) { illununit_ = p1; }

  public <R,A> R accept(com.biosimilarity.seleKt.model.ill.vm.illvm.Absyn.Instruction.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof com.biosimilarity.seleKt.model.ill.vm.illvm.Absyn.UNUNIT) {
      com.biosimilarity.seleKt.model.ill.vm.illvm.Absyn.UNUNIT x = (com.biosimilarity.seleKt.model.ill.vm.illvm.Absyn.UNUNIT)o;
      return this.illununit_.equals(x.illununit_);
    }
    return false;
  }

  public int hashCode() {
    return this.illununit_.hashCode();
  }


}

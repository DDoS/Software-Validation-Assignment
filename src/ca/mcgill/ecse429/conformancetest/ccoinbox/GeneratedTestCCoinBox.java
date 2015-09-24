package ca.mcgill.ecse429.conformancetest.ccoinbox;

import org.junit.Assert;
import org.junit.Test;

public class GeneratedTestCCoinBox {

    @Test
    public void conformanceTest0() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
    }

    @Test
    public void conformanceTest1() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
    }

    @Test
    public void conformanceTest2() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest3() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest4() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest5() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest6() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        final int curQtrs2 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs2 + 1, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest7() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        while (machine.getCurQtrs() != 2) {
            throw new UnsupportedOperationException("Missing event for reaching condition: curQtrs == 2");
        }
        final int totalQtrs = machine.getTotalQtrs();
        machine.vend();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(totalQtrs + 2, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest8() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        while (machine.getCurQtrs() != 3) {
            throw new UnsupportedOperationException("Missing event for reaching condition: curQtrs == 3");
        }
        final int totalQtrs = machine.getTotalQtrs();
        machine.vend();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(totalQtrs + 2, machine.getTotalQtrs());
        Assert.assertEquals(1, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest9() {
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        while (machine.getCurQtrs() <= 3) {
            throw new UnsupportedOperationException("Missing event for reaching condition: curQtrs > 3");
        }
        final int totalQtrs = machine.getTotalQtrs();
        final int curQtrs2 = machine.getCurQtrs();
        machine.vend();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(totalQtrs + 2, machine.getTotalQtrs());
        Assert.assertEquals(curQtrs2 - 2, machine.getCurQtrs());
    }
}

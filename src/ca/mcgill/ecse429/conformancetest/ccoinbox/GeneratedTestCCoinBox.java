package ca.mcgill.ecse429.conformancetest.ccoinbox;

import org.junit.Assert;
import org.junit.Test;

public class GeneratedTestCCoinBox {

    @Test
    public void conformanceTest0() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // returnQtrs [] / ; -> empty
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
    }

    @Test
    public void conformanceTest1() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // reset [] / totalQtrs = 0; -> empty
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
    }

    @Test
    public void conformanceTest2() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // returnQtrs [] / curQtrs = 0; -> empty
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest3() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // reset [] / totalQtrs = 0; curQtrs = 0; -> empty
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest4() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // returnQtrs [] / curQtrs = 0; allowVend = false; -> empty
        machine.returnQtrs();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest5() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // reset [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        machine.reset();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
    }

    @Test
    public void conformanceTest6() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> allowed
        final int curQtrs2 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs2 + 1, machine.getCurQtrs());
    }

    @Test
    public void conformanceTest7() {
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // vend [curQtrs == 2] / totalQtrs = totalQtrs + 2; curQtrs = 0; allowVend = false; -> empty
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
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // vend [curQtrs == 3] / totalQtrs = totalQtrs + 2; curQtrs = 1; allowVend = false; -> notAllowed
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
        // @ctor [] / totalQtrs = 0; curQtrs = 0; allowVend = false; -> empty
        final CCoinBox machine = new CCoinBox();
        Assert.assertEquals(CCoinBox.State.empty, machine.getState());
        Assert.assertEquals(0, machine.getTotalQtrs());
        Assert.assertEquals(0, machine.getCurQtrs());
        Assert.assertEquals(false, machine.getAllowVend());
        // addQtr [] / curQtrs = curQtrs + 1; -> notAllowed
        final int curQtrs = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.notAllowed, machine.getState());
        Assert.assertEquals(curQtrs + 1, machine.getCurQtrs());
        // addQtr [] / curQtrs = curQtrs + 1; allowVend = true; -> allowed
        final int curQtrs1 = machine.getCurQtrs();
        machine.addQtr();
        Assert.assertEquals(CCoinBox.State.allowed, machine.getState());
        Assert.assertEquals(curQtrs1 + 1, machine.getCurQtrs());
        Assert.assertEquals(true, machine.getAllowVend());
        // vend [curQtrs > 3] / totalQtrs = totalQtrs + 2; curQtrs = curQtrs - 2; -> allowed
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
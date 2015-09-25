package ca.mcgill.ecse429.conformancetest.legislation;

import org.junit.Assert;
import org.junit.Test;

public class GeneratedTestLegislation {

    @Test
    public void conformanceTest0() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInSenate [] / isCommonsBill = false; -> inSenate
        machine.introduceInSenate();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
        Assert.assertEquals(false, machine.getIsCommonsBill());
        // voteFails [] / ; -> inPreparation
        machine.voteFails();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
    }

    @Test
    public void conformanceTest1() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInSenate [] / isCommonsBill = false; -> inSenate
        machine.introduceInSenate();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
        Assert.assertEquals(false, machine.getIsCommonsBill());
        // votePasses [!getIsCommonsBill()] / ; -> inHouseOfCommons
        while (machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: !getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // voteFails [] / ; -> inPreparation
        machine.voteFails();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
    }

    @Test
    public void conformanceTest2() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInSenate [] / isCommonsBill = false; -> inSenate
        machine.introduceInSenate();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
        Assert.assertEquals(false, machine.getIsCommonsBill());
        // votePasses [!getIsCommonsBill()] / ; -> inHouseOfCommons
        while (machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: !getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // votePasses [getIsCommonsBill()] / ; -> inSenate
        while (!machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
    }

    @Test
    public void conformanceTest3() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInSenate [] / isCommonsBill = false; -> inSenate
        machine.introduceInSenate();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
        Assert.assertEquals(false, machine.getIsCommonsBill());
        // votePasses [!getIsCommonsBill()] / ; -> inHouseOfCommons
        while (machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: !getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // votePasses [!getIsCommonsBill()] / ; -> finalized
        while (machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: !getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.finalized, machine.getState());
    }

    @Test
    public void conformanceTest4() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInSenate [] / isCommonsBill = false; -> inSenate
        machine.introduceInSenate();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
        Assert.assertEquals(false, machine.getIsCommonsBill());
        // votePasses [getIsCommonsBill()] / ; -> finalized
        while (!machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.finalized, machine.getState());
    }

    @Test
    public void conformanceTest5() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInHouse [] / ; -> inHouseOfCommons
        machine.introduceInHouse();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // voteFails [] / ; -> inPreparation
        machine.voteFails();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
    }

    @Test
    public void conformanceTest6() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInHouse [] / ; -> inHouseOfCommons
        machine.introduceInHouse();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // votePasses [getIsCommonsBill()] / ; -> inSenate
        while (!machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.inSenate, machine.getState());
    }

    @Test
    public void conformanceTest7() {
        // @ctor [] / isCommonsBill = true; -> inPreparation
        final Legislation machine = new Legislation();
        Assert.assertEquals(Legislation.State.inPreparation, machine.getState());
        Assert.assertEquals(true, machine.getIsCommonsBill());
        // introduceInHouse [] / ; -> inHouseOfCommons
        machine.introduceInHouse();
        Assert.assertEquals(Legislation.State.inHouseOfCommons, machine.getState());
        // votePasses [!getIsCommonsBill()] / ; -> finalized
        while (machine.getIsCommonsBill()) {
            throw new UnsupportedOperationException("Missing event for reaching condition: !getIsCommonsBill()");
        }
        machine.votePasses();
        Assert.assertEquals(Legislation.State.finalized, machine.getState());
    }
}

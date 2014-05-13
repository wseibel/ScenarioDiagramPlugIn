/**
 */
package de.uks.se.scenariodiagram.tests;

import de.uks.se.scenariodiagram.ScenarioModel;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;

import junit.framework.TestCase;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Scenario Model</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ScenarioModelTest extends TestCase
{

  /**
   * The fixture for this Scenario Model test case.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ScenarioModel fixture = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static void main(String[] args)
  {
    TestRunner.run(ScenarioModelTest.class);
  }

  /**
   * Constructs a new Scenario Model test case with the given name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ScenarioModelTest(String name)
  {
    super(name);
  }

  /**
   * Sets the fixture for this Scenario Model test case.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void setFixture(ScenarioModel fixture)
  {
    this.fixture = fixture;
  }

  /**
   * Returns the fixture for this Scenario Model test case.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ScenarioModel getFixture()
  {
    return fixture;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see junit.framework.TestCase#setUp()
   * @generated
   */
  @Override
  protected void setUp() throws Exception
  {
    setFixture(ScenariodiagramFactory.eINSTANCE.createScenarioModel());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see junit.framework.TestCase#tearDown()
   * @generated
   */
  @Override
  protected void tearDown() throws Exception
  {
    setFixture(null);
  }

} //ScenarioModelTest

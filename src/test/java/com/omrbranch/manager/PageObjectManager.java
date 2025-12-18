package com.omrbranch.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.omrbranch.pages.BookHotelPage;
import com.omrbranch.pages.BookingConfirmPage;
import com.omrbranch.pages.ExploreHotelPage;
import com.omrbranch.pages.LoginPage;
import com.omrbranch.pages.MyBookingPage;
import com.omrbranch.pages.SelectHotelPage;

/**
 * ========================================================== PageObjectManager
 * ---------------------------------------------------------- Centralized
 * factory/manager for creating and reusing Page Object instances in the
 * framework.
 *
 * <p>
 * <b>Key Benefits:</b>
 * <ul>
 * <li>Lazy Initialization - pages are created only when needed</li>
 * <li>Single Instance Reuse - avoids duplicate page object creation</li>
 * <li>Improves Maintainability - centralized page access</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> This implementation is ideal for non-parallel execution where a
 * shared WebDriver (from BaseClass) is used.
 *
 * <p>
 * <b>Design Pattern:</b> Page Object Model (POM) + Lazy Initialization
 *
 * @author Velmurugan ==========================================================
 */
public class PageObjectManager {

  private static final Logger logger = LogManager.getLogger(PageObjectManager.class);

  private LoginPage loginPage;
  private ExploreHotelPage exploreHotelPage;
  private SelectHotelPage selectHotelPage;
  private BookHotelPage bookHotelPage;
  private BookingConfirmPage bookingConfirmPage;
  private MyBookingPage myBookingPage;

  /**
   * Returns the {@link LoginPage} instance. Creates a new instance only if it
   * does not already exist.
   *
   * @return LoginPage instance
   */
  public LoginPage getLoginPage() {
    if (loginPage == null) {
      logger.info("Initializing LoginPage...");
      loginPage = new LoginPage();
    }
    return loginPage;
  }

  /**
   * Returns the {@link ExploreHotelPage} instance. Creates a new instance only if
   * it does not already exist.
   *
   * @return ExploreHotelPage instance
   */
  public ExploreHotelPage getExploreHotelPage() {
    if (exploreHotelPage == null) {
      logger.info("Initializing ExploreHotelPage...");
      exploreHotelPage = new ExploreHotelPage();
    }
    return exploreHotelPage;
  }

  /**
   * Returns the {@link SelectHotelPage} instance. Creates a new instance only if
   * it does not already exist.
   *
   * @return SelectHotelPage instance
   */
  public SelectHotelPage getSelectHotelPage() {
    if (selectHotelPage == null) {
      logger.info("Initializing SelectHotelPage...");
      selectHotelPage = new SelectHotelPage();
    }
    return selectHotelPage;
  }

  /**
   * Returns the {@link BookHotelPage} instance. Creates a new instance only if it
   * does not already exist.
   *
   * @return BookHotelPage instance
   */
  public BookHotelPage getBookHotelPage() {
    if (bookHotelPage == null) {
      logger.info("Initializing BookHotelPage...");
      bookHotelPage = new BookHotelPage();
    }
    return bookHotelPage;
  }

  /**
   * Returns the {@link BookingConfirmPage} instance. Creates a new instance only
   * if it does not already exist.
   *
   * @return BookingConfirmPage instance
   */
  public BookingConfirmPage getBookingConfirmPage() {
    if (bookingConfirmPage == null) {
      logger.info("Initializing BookingConfirmPage...");
      bookingConfirmPage = new BookingConfirmPage();
    }
    return bookingConfirmPage;
  }

  /**
   * Returns the {@link MyBookingPage} instance. Creates a new instance only if it
   * does not already exist.
   *
   * @return MyBookingPage instance
   */
  public MyBookingPage getMyBookingPage() {
    if (myBookingPage == null) {
      logger.info("Initializing MyBookingPage...");
      myBookingPage = new MyBookingPage();
    }
    return myBookingPage;
  }
}

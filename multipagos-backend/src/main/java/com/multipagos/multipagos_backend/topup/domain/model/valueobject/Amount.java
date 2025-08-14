package com.multipagos.multipagos_backend.topup.domain.model.valueobject;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Amount Value Object
 * Encapsulates monetary amount validation and business rules for top-ups
 * Immutable and self-validating following DDD principles
 */
@Value
public class Amount {

  private static final BigDecimal MIN_AMOUNT = new BigDecimal("1000");
  private static final BigDecimal MAX_AMOUNT = new BigDecimal("100000");
  private static final Currency CURRENCY = Currency.getInstance("COP");
  private static final int SCALE = 2;

  BigDecimal value;

  /**
   * Create an Amount with validation
   * 
   * @param value the monetary amount
   * @throws IllegalArgumentException if amount is invalid
   */
  public Amount(BigDecimal value) {
    if (value == null) {
      throw new IllegalArgumentException("El valor es requerido");
    }

    BigDecimal scaledValue = value.setScale(SCALE, RoundingMode.HALF_UP);

    if (scaledValue.compareTo(MIN_AMOUNT) < 0) {
      throw new IllegalArgumentException(
          String.format("El valor debe ser mayor o igual a %s", formatCurrency(MIN_AMOUNT)));
    }

    if (scaledValue.compareTo(MAX_AMOUNT) > 0) {
      throw new IllegalArgumentException(
          String.format("El valor debe ser menor o igual a %s", formatCurrency(MAX_AMOUNT)));
    }

    if (scaledValue.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El valor debe ser positivo");
    }

    this.value = scaledValue;
  }

  /**
   * Create Amount from long value (treating as cents)
   */
  public Amount(long cents) {
    this(new BigDecimal(cents).divide(new BigDecimal("100")));
  }

  /**
   * Create Amount from string
   */
  public Amount(String value) {
    this(new BigDecimal(value));
  }

  /**
   * Static factory methods
   */
  public static Amount of(BigDecimal value) {
    return new Amount(value);
  }

  public static Amount of(long cents) {
    return new Amount(cents);
  }

  public static Amount of(String value) {
    return new Amount(value);
  }

  public static Amount minimum() {
    return new Amount(MIN_AMOUNT);
  }

  public static Amount maximum() {
    return new Amount(MAX_AMOUNT);
  }

  /**
   * Business rules
   */
  public boolean isValidForTopUp() {
    return value.compareTo(MIN_AMOUNT) >= 0 && value.compareTo(MAX_AMOUNT) <= 0;
  }

  public boolean isMaximumAmount() {
    return value.compareTo(MAX_AMOUNT) == 0;
  }

  /**
   * Formatting
   */
  public String toCurrencyString() {
    return formatCurrency(value);
  }

  public String toPlainString() {
    return value.toPlainString();
  }

  private static String formatCurrency(BigDecimal amount) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
    formatter.setCurrency(CURRENCY);
    return formatter.format(amount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Amount))
      return false;
    Amount amount = (Amount) o;
    return Objects.equals(value, amount.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return toCurrencyString();
  }
}

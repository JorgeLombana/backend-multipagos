package com.multipagos.multipagos_backend.topup.domain.model;

import com.multipagos.multipagos_backend.topup.domain.model.valueobject.Amount;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.PhoneNumber;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.SupplierId;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.SupplierType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Supplier Domain Model
 * Rich domain object with behavior and business rules
 * No longer anemic - encapsulates supplier business logic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

  private SupplierId supplierId;
  private String name;
  private boolean active;
  private LocalDateTime lastUpdated;

  /**
   * Constructor for backward compatibility
   */
  public Supplier(String id, String name) {
    this.supplierId = SupplierId.of(id);
    this.name = name;
    this.active = true;
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Factory method from SupplierType
   */
  public static Supplier fromType(SupplierType type) {
    return new Supplier(
        SupplierId.of(type),
        type.getName(),
        true,
        LocalDateTime.now());
  }

  /**
   * Backward compatibility methods
   */
  public String getId() {
    return supplierId != null ? supplierId.getValue() : null;
  }

  public void setId(String id) {
    this.supplierId = id != null ? SupplierId.of(id) : null;
  }

  /**
   * Business logic methods
   */
  public boolean isActive() {
    return active;
  }

  public void activate() {
    this.active = true;
    this.lastUpdated = LocalDateTime.now();
  }

  public void deactivate() {
    this.active = false;
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Business rule: Check if supplier supports a specific phone number
   */
  public boolean supportsPhoneNumber(PhoneNumber phoneNumber) {
    if (!isActive() || supplierId == null) {
      return false;
    }
    return supplierId.supportsPhoneNumber(phoneNumber);
  }

  /**
   * Business rule: Check if supplier can process a top-up request
   */
  public boolean canProcessTopUp(TopUpRequest request) {
    if (!isActive()) {
      return false;
    }

    if (request == null || request.getPhoneNumber() == null || request.getSupplierId() == null) {
      return false;
    }

    // Check if this supplier matches the request
    if (!Objects.equals(this.supplierId, request.getSupplierId())) {
      return false;
    }

    // Check if supplier supports the phone number
    return supportsPhoneNumber(request.getPhoneNumber());
  }

  /**
   * Business rule: Validate top-up amount for this supplier
   */
  public boolean isValidAmountForTopUp(Amount amount) {
    if (!isActive() || amount == null) {
      return false;
    }

    // All suppliers use the same amount rules for now
    // This could be extended to have per-supplier rules
    return amount.isValidForTopUp();
  }

  /**
   * Get supplier type enum
   */
  public SupplierType getType() {
    return supplierId != null ? supplierId.getType() : null;
  }

  /**
   * Update supplier information
   */
  public void updateInformation(String newName) {
    if (newName != null && !newName.trim().isEmpty()) {
      this.name = newName.trim();
      this.lastUpdated = LocalDateTime.now();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Supplier))
      return false;
    Supplier supplier = (Supplier) o;
    return Objects.equals(supplierId, supplier.supplierId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supplierId);
  }

  @Override
  public String toString() {
    return String.format("Supplier{id='%s', name='%s', active=%s}",
        getId(), name, active);
  }
}

package com.restoforge.api.stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Produit du catalogue, rattaché à un fournisseur unique.
 *
 * <p>
 * Le niveau de stock est porté par le produit lui-même (état courant plutôt
 * que journal de mouvements — voir ADR-005).
 */
@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String name;

  /** Unité de vente : kg, L, pièce… */
  @Column(nullable = false, length = 20)
  private String unit;

  /** Prix catalogue courant. Recopié sur la ligne de commande à sa création. */
  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  /**
   * LAZY explicite : le défaut d'un @ManyToOne est EAGER, qui irait chercher le
   * fournisseur à chaque produit chargé.
   */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "supplier_id", nullable = false)
  private Supplier supplier;

  /** Seuil sous lequel le produit est proposé au réapprovisionnement. */
  @Column(name = "alert_threshold", nullable = false, precision = 10, scale = 3)
  private BigDecimal alertThreshold = BigDecimal.ZERO;

  /**
   * Quantité actuellement en stock. Incrémentée à la réception d'une commande.
   */
  @Column(nullable = false, precision = 10, scale = 3)
  private BigDecimal stock = BigDecimal.ZERO;

  /** Requis par Hibernate — ne pas utiliser dans le code métier. */
  protected Product() {
  }

  public Product(String name, String unit, BigDecimal unitPrice, Supplier supplier,
      BigDecimal alertThreshold) {
    this.name = name;
    this.unit = unit;
    this.unitPrice = unitPrice;
    this.supplier = supplier;
    this.alertThreshold = alertThreshold;
    this.stock = BigDecimal.ZERO;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Supplier getSupplier() {
    return supplier;
  }

  public void setSupplier(Supplier supplier) {
    this.supplier = supplier;
  }

  public BigDecimal getAlertThreshold() {
    return alertThreshold;
  }

  public void setAlertThreshold(BigDecimal alertThreshold) {
    this.alertThreshold = alertThreshold;
  }

  /**
   * Pas de setter : le stock ne se pilote pas librement. Sa seule évolution
   * prévue au MVP est l'incrément à la réception d'une commande (RES-13), qui
   * portera une méthode métier dédiée.
   */
  public BigDecimal getStock() {
    return stock;
  }
}
package com.aydsii.demo.cu09.dominio;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;
    private LocalDateTime fechaHora;
    private String estado;
    private BigDecimal montoTotal;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario cliente;

    // Getters y Setters
    public Long getIdPedido() { return idPedido; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }public void setEstado(String estado) {
        this.estado = estado;
    }public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
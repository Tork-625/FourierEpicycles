package com.epicycles.utils;

public class ComplexNumber {
    public double real;
    public double imaginary;

    public ComplexNumber(double real, double imag) {
        this.real = real;
        this.imaginary = imag;
    }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, this.imaginary - other.imaginary);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(
                (this.real * other.real) - (this.imaginary * other.imaginary),
                (this.real * other.imaginary + this.imaginary * other.real)
        );
    }

    public ComplexNumber divide(double scalar) {
        return new ComplexNumber(this.real / scalar, this.imaginary / scalar);
    }
}

package com.epicycles.fourier;

import com.epicycles.utils.ComplexNumber;

public class FourierComponent {
    public double frequency;
    public ComplexNumber value;


    public FourierComponent(double frequency, ComplexNumber value) {
        this.frequency = frequency;
        this.value = value;
    }


    public double getAmplitude() {
        return Math.sqrt(value.real * value.real + value.imaginary * value.imaginary);
    }


    public double getPhase() {
        return Math.atan2(value.imaginary, value.real);
    }
}
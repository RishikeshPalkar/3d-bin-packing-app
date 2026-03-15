import React from 'react';
import { View, Text, TextInput, StyleSheet } from 'react-native';

interface Props {
    label: string;
    value: string;
    onChange: (val: string) => void;
}

export function DimensionInput({ label, value, onChange }: Props) {
    return (
        <View style={styles.wrap}>
        <Text style={styles.label}>{label}</Text>
            <TextInput
    style={styles.input}
    value={value}
    onChangeText={onChange}
    keyboardType="decimal-pad"
    placeholder="0"
    placeholderTextColor="#3A4048"
        />
        </View>
);
}

const styles = StyleSheet.create({
    wrap: { flex: 1, marginHorizontal: 4 },
    label: { fontSize: 11, color: '#5C6480', marginBottom: 4, letterSpacing: 0.5 },
    input: {
        backgroundColor: '#1A1D20',
        borderWidth: 1,
        borderColor: '#252A2D',
        color: '#E2E6F0',
        paddingHorizontal: 10,
        paddingVertical: 9,
        fontSize: 15,
        borderRadius: 6,
        textAlign: 'center',
    },
});
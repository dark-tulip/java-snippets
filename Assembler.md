# Solve expression on TASM example
 
This may help for:
* Input number in assembler,
* Solve expression
* Read data and print the message

Note: works only for small numbers


How it works:

![image](https://user-images.githubusercontent.com/89765480/163720576-05707d5b-ca7d-457d-b7a3-b4a913a42121.png)


```asm
; The program to solve given expression
; Z=((a+2)*(b-2))/((c-d)^2) 

; can be build ON GUI TURBO ASSEMBLER x64 v3.0.1

.model small
 
.stack 100h
 
.data
a       dw      ?
b       dw      ?
c       dw      ?
d       dw      ?
z       dw      ?
 
inputA  db      10, 13, 'A = $', 10, 13
inputB  db      'B = $', 10, 13
inputC  db      'C = $', 10, 13
inputD  db      'D = $', 10, 13
 
resultZ db      'Z = ((a+2)*(b-2))/((c-d)^2) = ', '$'
 
.code
 
; input decimal number to AX
input_number     proc
        push    cx              ; saving registers on stack
        push    bx
        push    dx
        push    si
        mov     si,     0       ; this is the sign flag
        xor     cx,     cx
        mov     bx,     10
        call    input_digit     ; call the func to input digit
 
        cmp     al,     '-'     ; if we have negative number input the flag
        je      @@sign_flag
        jmp     @@inputChar     ; else input this character
 
@@sign_flag:
        mov     si,     1
 
input:
        call    input_digit     ; input next character
 
@@inputChar:
        cmp     al,     13      ; Enter ?
        je      done            ; yes -> complete procedure
        sub     al,     '0'     ; no  -> convert ascii char -> to int (for solving)
        xor     ah,     ah      
        xor     dx,     dx
        xchg    cx,     ax
        mul     bx
        add     ax,     cx
        xchg    ax,     cx
        jmp     input
done:
        xchg    ax,     cx
        cmp     si,     1       ; compare  
        je      @@inverse_ax    ; jump if sign flag enabled
        jmp     @@exit          ; else exit
 
@@inverse_ax:
        neg     ax
@@exit:
        pop     si
        pop     dx
        pop     bx
        pop     cx
        ret
input_number endp
 
; signal to input one character
input_digit proc
        mov     ah,     1
        int     21h
        ret
input_digit endp
 
 
; The procedure to prints value of AX
print_result proc
        push    ax
        push    bx
        push    cx
        push    dx
        push    di
 
        mov     cx, 10
        xor     di, di          ; di - the sum of digits in number
 
        ; if num in ax is negative
        or      ax, ax
        jns     @@ascii_convert
        push    ax
        mov     dx, '-'         ; print '-'
        mov     ah, 2           ; ah - syscall to print
        int     21h
        pop     ax
 
        neg     ax              ; make ax positive
 
@@ascii_convert:
        xor     dx, dx
        div     cx              ; dl = num mod 10
        add     dl, '0'         ; dl = (dl + 48)
        inc     di              ; increment di
        push    dx              ; append to stack
        or      ax, ax          
        jnz     @@ascii_convert ; jump if not zero
 
        ; print from stack to screen
@@print_digit:
        pop     dx              ; dl = character
        mov     ah, 2           ; ah - func to print (dos syscall)
        int     21h
        dec     di              ; while have digits, di != 0
        jnz     @@print_digit
 
        pop     di
        pop     dx
        pop     cx
        pop     bx
        pop     ax
        ret
print_result endp
 
main    proc
        mov     ax,     @data
        mov     ds,     ax;
 
        ;input a
        mov     ah,     09h
        lea     dx,     inputA  ; lea like offset, but works on realtime working program, offset calculates on compiling
        int     21h
        call    input_number
        mov     a,      ax
 
        ;input B
        mov     ah,     09h
        lea     dx,     inputB
        int     21h
        call    input_number
        mov     b,      ax
       
        ;input C
        mov     ah,     09h
        lea     dx,     inputC
        int     21h
        call    input_number
        mov     c,      ax
       
        ;input D
        mov     ah,     09h
        lea     dx,     inputD
        int     21h
        call    input_number
        mov     d,      ax
 
        ;  ==============  COUNT Z =((a+2)*(b-2))/((c-d)^2)
        mov     ax,     a
        add     ax,     2  ; (a+2)
        mov     bx,     b  
        sub     bx,     2  ; (b-2)
        imul    bx         ; AX = (a+2)*(b-2)
        mov     a, ax      ; save value on AX
       
        xor     cx, cx
        mov     cx, c      
        sub     cx, d      ; (c-d)
        mov     ax, cx     ; save (c-d) on AX
        imul    ax         ; ax = ax*ax which is (c-d)*(c-d)
       
        mov     c, ax      ; save (c-d)*(c-d) on C variable
        mov     ax, a      ; set AX = value of A variable
       
        idiv    c          ; ((a+2)*(b-2))/((c-d)^2)
        mov     z, ax      ; save result to Z variable
       
        ; print result
        mov     ah,     09h
        lea     dx,     resultZ  ; often used on ually stack with recalculated values
        int     21h
        mov     ax, z      ; save on AX the value to print
        call    print_result
 
        mov     ah,0
        int     16h
 
        mov     ax,4c00h   ; syscall
        int     21h
main    endp
 
end     main
```

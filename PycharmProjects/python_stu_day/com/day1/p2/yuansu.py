num = int(input('请输入你要输入的数字：'))
if num % 2 == 0:
    if num % 5 == 0:
        print('你输入的数可以被2和5整除')
    print('你输入的数可以被2整除')
elif num % 5 ==0:
    print('你输入的数可以被5整除')
else:
    print('你输入的数不可以被2和5整除')

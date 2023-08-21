#这个脚本要配合bingchat来使用
#使用之前请向bingchat提交预设：将我之后给你的列表全部翻译成中文，保留符号，保留格式，保留顺序，不要有多余的废话，不翻译原有的中文，不搜索任何东西，框在代码框理面




# 导入os模块，用于操作文件和目录
import re
import os
import time

import pyperclip
# 定义被替换和替换的文件的路径
# 被替换路径 =input("未汉化hack路径：")
# 替换路径 =input("已汉化hack路径：")


# 定义一个函数来获取文件中的双引号之间的字符串，并返回一个列表和文件内容
def 获取字符串(文件):
    # 打开文件
    with open(文件, "r", encoding="utf-8") as f:
        # 读取文件内容
        内容 = f.read()
        # 定义一个列表来存储字符串
        字符串列表 = []
        # 查找@SearchTags的位置
        搜索标签位置 = 内容.find("@SearchTags")
        # 如果找到了
        if 搜索标签位置 != -1:
            # 查找第一个左括号和右括号的位置
            左括号位置 = 内容.find("(", 搜索标签位置)
            右括号位置 = 内容.find(")", 左括号位置)
            # 如果找到了，将开始位置更新为右括号之后
            if 左括号位置 != -1 and 右括号位置 != -1:
                开始位置 = 右括号位置 + 1
            else:
                # 否则，将开始位置设为0
                开始位置 = 0
        else:
            # 否则，将开始位置设为0
            开始位置 = 0
        # 查找双引号之间的字符串
        while True:
            # 查找下一个双引号的位置
            位置 = 内容.find('"', 开始位置)
            # 如果没有找到，跳出循环
            if 位置 == -1:
                break
            # 查找当前行的末尾的位置
            行尾位置 = 内容.find("\n", 位置)
            # 如果没有找到，将行尾设为内容的长度
            if 行尾位置 == -1:
                行尾位置 = len(内容)
            # 提取当前行的内容，并判断是否包含"minecraft:"字符串
            当前行 = 内容[开始位置:行尾位置]
            if "minecraft:" in 当前行:
                # 如果包含，跳过这一行，更新开始位置为下一行的开头
                开始位置 = 行尾位置 + 1
                continue
            else:
                # 如果不包含，查找下一个双引号的位置
                结束位置 = 内容.find('"', 位置 + 1)
                # 如果没有找到，跳出循环
                if 结束位置 == -1:
                    break
                # 提取双引号之间的字符串，并添加到列表中
                字符串 = 内容[位置 + 1:结束位置]
                字符串列表.append(字符串)
                # 更新开始位置为下一个双引号之后
                开始位置 = 结束位置 + 1

        return 字符串列表, 内容
def 找到super(文件):
    # 打开文件
    with open(文件, "r", encoding="utf-8") as f:
        # 读取文件内容
        内容 = f.read()
        # 定义一个列表来存储字符串
        字符串列表 = []
        # 将内容按行分割
        行列表 = 内容.splitlines()
        # 遍历每一行
        for 行 in 行列表:
            # 找到super("的位置
            位置 = 行.find("super(")
            # 如果找到了
            if 位置 != -1:
                # 从那个位置开始找下一个引号的位置
                结束位置 = 行.find('"', 位置 + 7)
                # 如果找到了
                if 结束位置 != -1:
                    # 提取引号之间的内容，并添加到列表中
                    字符串 = 行[位置 + 7:结束位置]
                    字符串列表.append(字符串)

        return 字符串列表,内容
print("1：对比替换2：翻译英文名称3:翻译功能名称")
mode = input("选择模式:")



复制 = []
js = 0

#遍历源文件目录下的所有文件
if mode == "1":
    被替换路径 = input("未汉化hack路径：")
    替换路径 = input("已汉化hack路径：")
    #对比替换
    for 根目录, 子目录, 文件名 in os.walk(替换路径):
        for i in 文件名:
            # 获取源文件中的字符串列表和内容
            源文件引号内名, 源文件内容 = 获取字符串(os.path.join(根目录, i))
            次数 = 0
            js += 1
            # 遍历被替换文件目录下的所有文件名
            for 根目录2, 子目录2, 文件名2 in os.walk(被替换路径):
                # 如果被替换文件中有字符串，获取字符串列表和内容
                y = os.path.exists(os.path.join(根目录2, i))
                if os.path.exists(os.path.join(根目录2, i)) == False:
                    pass
                else:
                    if 获取字符串(os.path.join(根目录2, i))[0] != []:
                        被替换引号内名, 被替换内容 = 获取字符串(os.path.join(根目录2, i))
                    # 遍历被替换文件中的每个字符串
                    while 次数 != len(被替换引号内名):
                        if 次数 >= 1000:
                            print(fr"{根目录2}\{i}响应时间过长，已跳过")
                            break
                        else:
                            # 如果源文件中有相同的字符串，从列表中移除
                            if len(获取字符串(os.path.join(根目录, i))[0]) != len(获取字符串(os.path.join(根目录2, i))[0]):

                                # 如果源文件和被替换文件中的字符串数量不同，打印提示信息并复制到剪切板
                                if len(获取字符串(os.path.join(根目录, i))[0]) != len(获取字符串(os.path.join(根目录2, i))[0]):
                                    # if 源文件引号内名[0] == (被替换引号内名[次数]):
                                    #     源文件引号内名.remove(源文件引号内名[0])
                                    #     pass
                                    # else:
                                    pyperclip.copy(f"{被替换引号内名}")
                                    print(被替换引号内名)
                                    输入 = input("被替换引号内名:")
                                    if 输入 == "":
                                        pass
                                    else:
                                        输入 = list(eval(输入))
                                    # 用自定义的字符串替换被替换文件中的字符串
                                    列表1234 = ""
                                    for 列表1234 in 输入:
                                        lb = str(列表1234)
                                        lb1 = lb.replace("\n","\\n")
                                        新内容 = 被替换内容.replace(f'"{被替换引号内名[次数]}"', fr'"{lb1}"')
                                        被替换内容 = 新内容
                                        # 写入新内容到被替换文件中
                                        with open(os.path.join(根目录2, i), "w", encoding="UTF-8") as f:
                                            f.seek(0)
                                            f.write(新内容)
                                        次数 += 1
                                    break
                            else:
                                if 源文件引号内名 == []:
                                    次数 += 1
                                    pass
                                else:
                                    # 用第一个字符串替换被替换文件中的字符串
                                    新内容 = 被替换内容.replace(f'"{被替换引号内名[次数]}"', f'"{源文件引号内名[0]}"')
                                    被替换内容 = 新内容
                                    # 写入新内容到被替换文件中
                                    with open(os.path.join(根目录2, i), "w", encoding="UTF-8") as f:
                                        f.seek(0)
                                        f.write(新内容)
                                        # 从列表中移除第一个字符串
                                        源文件引号内名.remove(f"{源文件引号内名[0]}")
                            次数 += 1
                    print(i)
elif mode == "2":
    被替换路径 = input("未汉化hack路径：")
    #翻译英文
    for 根目录, 子目录, 文件名 in os.walk(被替换路径):
        skip_list = ["altmanager", "analytics", "event", "events", "hud", "mixin", "util"]
        skip = any(word in 根目录 for word in skip_list)
        if skip:
            pass
        else:
            for i in 文件名:
                # 获取源文件中的字符串列表和内容
                源文件引号内名, 源文件内容 = 获取字符串(os.path.join(根目录, i))
                次数 = 0
                js += 1
                # 遍历被替换文件目录下的所有文件名
                if 获取字符串(os.path.join(根目录, i))[0] != []:
                    被替换引号内名, 被替换内容 = 获取字符串(os.path.join(根目录, i))
                if re.match("^[a-zA-Z]+$", 被替换引号内名[0]):
                # 遍历被替换文件中的每个字符串
                    while 次数 != len(被替换引号内名):
                        if 次数 >= 1000:
                            print(fr"{根目录}\{i}响应时间过长，已跳过")
                            break
                        else:
                            pyperclip.copy(f"{被替换引号内名}")
                            print("""
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            """)
                            print(f"-----------------------{os.path.join(根目录,i)}")
                            print(被替换引号内名)
                            输入 = input("被替换引号内名:")
                            if 输入 == "":
                                输入 = []
                            else:
                                输入 = list(eval(输入))
                            # 用自定义的字符串替换被替换文件中的字符串
                            列表1234 = ""
                            for 列表1234 in 输入:
                                lb = str(列表1234)
                                lb1 = lb.replace("\n", "\\n")
                                新内容 = 被替换内容.replace(f'"{被替换引号内名[次数]}"', fr'"{lb1}"')
                                被替换内容 = 新内容
                                # 写入新内容到被替换文件中
                                with open(os.path.join(根目录, i), "w", encoding="UTF-8") as f:
                                    f.seek(0)
                                    f.write(新内容)
                                次数 += 1
                            break
                        次数 += 1

elif mode == "3":
    被替换路径 = input("未汉化hack路径：")
    替换路径 = input("已汉化hack路径：")
    try:
        for 根目录, 子目录, 文件名 in os.walk(替换路径):
            for i in 文件名:
                js += 1
                print(i)
                # 获取源文件中的字符串列表和内容
                源文件super = 找到super(os.path.join(根目录, i))[0]
                for 根目录2, 子目录2, 文件名2 in os.walk(被替换路径):
                    # 如果被替换文件中有字符串，获取字符串列表和内容
                    y = os.path.exists(os.path.join(根目录2, i))
                    if os.path.exists(os.path.join(根目录2, i)) == False:
                        pass
                    else:
                        被替换super = 找到super(os.path.join(根目录2, i))[0]
                        被替换内容 = 找到super(os.path.join(根目录2, i))[1]
                        新内容 = 被替换内容.replace(f'super("{被替换super[0]}', f'super("{源文件super[0]}')
                        被替换内容 = 新内容
                        with open(os.path.join(根目录2, i), "w", encoding="UTF-8") as f:
                            f.seek(0)
                            f.write(新内容)

    except:
        pass

print(f"翻译{js}个")
time.sleep(1)
os.system("pause");
### Ссылки

 - [Концепция](concept.md)
 - [Проект-пример mvc.war.example]
 - [Спецификация контроллеров](controller_cpec.md)

### Проект-пример mvc.war.example

Для запуска проекта примера необходимо сначала установить следующее ПО:
 - java jdk 1.8+
 - gradle 3.5+
 - git 2.7.4+

После этого скачать репозиторий с проектом запустив команду:

    git clone https://github.com/greetgo/greetgo.mvc.git

Зайти в директорию:

    cd greetgo.mvc/greetgo.mvc.parent/

И запустить команду:

    gradle runMvcWarExample

Эта команда соберёт проект-пример и запустит томкат с этим проектом. В конце вылетит сообщение:

    The Server is running at http://localhost:8080/mvc_example

Пройдя в браузере по указанной ссылке, попадаем в приложение-пример
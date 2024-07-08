```
git stage - синоним для команды git add
git merge - слияние двух веток изменений
git revert - откатить коммит
git cherry pick - заново добавить откаченный коммит
```

### git reset
флаги (soft, mixed, hard) default is mixed

- рабочий каталог это то где ведется работа
- индекс область изменений включаемых в коммит
- HEAD указатель на последний коммит в текущей ветке

### git add
индексация в локальном workspace начинается после этой команды


### git commit
фикс локальных изменений для отправки на удаленный сервер

### git fetch
стянуть информацию об изменениях на удаленном сервере

### git pull (or rebase)
- стянуть удаленные изменения

## Git merge git rebase
- использовать rebase плохая идея так как она стирает историю коммитов
- not best practice to use rebase when multiple users work on same feature branch, because it's deleting commits history
- rebase looks clearly, but changes commit hashes
